# Campaign Management Service (CampaignService)

## Overview
The `CampaignService` is responsible for all advertiser-facing operations. It acts as the canonical source of truth for advertiser profiles, campaign configurations, and creative assets. It provides a self-serve portal for advertisers to manage their advertising initiatives within the Food Delivery platform.

## High-Level System Architecture
Below is the overview of all 5 bounded contexts and their inter-service communication within the advertisement platform.

```mermaid
graph TB
    subgraph External["External Systems"]
        SSP["SSP / Ad Exchange"]
        Browser["User Browser / Native SDK"]
        Advertiser["Advertiser Portal"]
    end

    subgraph CorePlatform["Core Food Delivery Platform"]
        BWS["WalletService (Generic)"]
        Ledger["LedgerService"]
        BillPay["PaymentService"]
    end

    subgraph Platform["Advertisement Platform - Kubernetes Cluster"]
        GW["API Gateway<br/>Rate Limiting · TLS · Load Shedding"]

        subgraph CS["CampaignService"]
            CS_API["REST/gRPC APIs"]
            CS_DB[("PostgreSQL<br/>ACID")]
            CS_CDN["CDN<br/>Creative Assets"]
        end

        subgraph BE["BiddingEngine"]
            Parser["OpenRtbRequestParser<br/>Protobuf / JSON"]
            Cache["Multi-Tier Cache<br/>L1: Caffeine · L2: Redis"]
            Index["RoaringBitmap<br/>Inverted Index"]
            Matcher["CampaignMatcher"]
            Pricer["BidPricer<br/>PricingStrategy"]
            Disruptor["LMAX Disruptor<br/>Ring Buffer"]
        end

        subgraph BPS["BudgetLimitingService"]
            PacingLoop["Async Control Loop"]
            MLModel["ML CTR Model"]
            BPS_Store[("Time-Series DB<br/>+ Redis")]
        end

        subgraph ETS["UserTrackingService"]
            TrackEndpoint["Tracking Pixel Endpoint"]
            Decrypt["Price Decryption<br/>Crypto Keys"]
            Trackers["ImpressionTracker<br/>ClickTracker<br/>ConversionTracker"]
        end

        Kafka["Apache Kafka"]
        ClickHouse[("ClickHouse<br/>OLAP")]
        OTel["OpenTelemetry<br/>Distributed Tracing"]
    end

    Advertiser -->|"Auth / CRUD"| GW
    GW --> CS_API
    CS_API --> CS_DB
    CS_API -->|"Upload Creatives"| CS_CDN
    CS_API -->|"Campaign Changes"| Kafka

    SSP -->|"BidRequest<br/>Protobuf"| GW
    GW --> Parser
    Parser --> Cache
    Cache --> Index
    Index --> Matcher
    Matcher --> Disruptor
    Disruptor --> Pricer
    Pricer -->|"BidResponse<br/>+ SeatBid"| GW
    GW -->|"< 50ms"| SSP

    Kafka -->|"Index Updates"| Index
    Kafka -->|"Pacing Events"| PacingLoop
    PacingLoop --> MLModel
    PacingLoop --> BPS_Store
    BPS_Store -->|"Pacing Multiplier s"| Cache

    Browser -->|"Tracking Pixel"| TrackEndpoint
    TrackEndpoint --> Decrypt
    Decrypt --> Trackers
    Trackers --> Disruptor
    Disruptor -->|"Async Buffer"| Kafka
    Kafka --> ClickHouse
    Kafka -->|"Billing Event"| BWS
    BWS -->|"Step 1: Saga"| BWS
    BWS -->|"Step 2"| Ledger
    Ledger -->|"Step 3"| BillPay

    OTel -.->|"Traces"| GW
    OTel -.->|"Traces"| BE
    OTel -.->|"Traces"| BPS
    OTel -.->|"Traces"| ETS
    OTel -.->|"Traces"| BWS
```

## Core Responsibilities
- **Advertiser Profiles**: Management of advertiser registration, authentication, and organizational profiles.
- **Campaign Management**: CRUD operations for Campaigns, Ad Groups, and Ad Creatives (supporting banner, video VAST, and native formats).
- **Targeting Configurations**: Managing contextual, geographic, demographic, and behavioral targeting rules. Supports defining blocklists for brand safety and dayparting rules.
- **Budget Management**: Defining daily and lifetime financial budgets.
- **Creative Storage**: Handling creative asset uploads to object storage (e.g., S3/GCS) fronted by a CDN for low-latency global serving.
- **State Management**: Managing campaign lifecycle states (Active, Paused, Completed, Archived).

## Use Case Validation
The platform is generalized to gracefully handle vastly different use cases without requiring customized deployments:

| Business Entity Profile | Primary Use Case and Campaign Objective | Targeting and Bidding Strategy |
| :--- | :--- | :--- |
| **Local Independent Restaurant** | Drive foot traffic during lunch and dinner hours. | Hyper-local geo-targeting (e.g., 5-mile radius), dayparting (11 AM - 2 PM), contextual food-related inventory. Strict pacing for minimal budget. |
| **National E-Commerce Retailer** | Drive direct online sales and retarget abandoned carts. | Behavioral retargeting using first-party tracking pixels, broad geo-targeting, and Dynamic Creative Optimization (DCO). Relies heavily on bid shading to maximize ROI. |
| **B2B Software Corporation** | Generate high-quality leads and whitepaper downloads. | Demographic targeting, intent-based contextual keywords, high maximum bids for premium publisher inventory. Prioritizes impression quality over volume. |

## Architecture & Integration
The `CampaignService` utilizes a strict ACID PostgreSQL database to maintain decentralized data specific to campaigns and advertisers. It integrates with `RestaurantApplication` via FeignClient for restaurant owners to create and track campaigns.

**Data Flow (Campaign Management → Index Update Flow):**
How campaign changes propagate to the `BiddingEngine`'s in-memory index in real time.

```mermaid
sequenceDiagram
    autonumber
    participant Adv as Advertiser
    participant CS as CampaignService
    participant PG as PostgreSQL
    participant Kafka as Apache Kafka
    participant BE as BiddingEngine
    participant Index as RoaringBitmap Index

    Adv->>CS: Create/Update/Pause Campaign
    CS->>CS: Validate data (budgets, targeting, creative)
    CS->>CS: Optimistic Lock check (version column)
    CS->>PG: Write to database (ACID)
    CS->>Kafka: Publish CampaignChangeEvent

    Note over CS,Kafka: Creative audit: only Approved creatives are indexed

    Kafka->>BE: Consume event (with offset tracking)

    alt Campaign Created / Updated
        BE->>Index: Build new RoaringBitmap snapshot
        BE->>Index: RCU swap — atomic reference update
        Note over Index: No read threads blocked during update
    else Campaign Paused
        BE->>Index: Remove campaign from bitmap
        Note over BE: In-flight bids still honored for billing
    else Campaign Budget Exhausted
        BE->>Index: Remove campaign from bitmap
    end

    alt Consumer lag detected
        BE->>Kafka: Replay from last committed offset
        Note over BE: Eventual consistency guaranteed
    end
```

## Resilience & Edge Cases
- **Creative Audit**: Integrates a creative status workflow (Pending → Approved → Rejected). Only approved creatives are indexed by the `BiddingEngine`.
- **Targeting Complexity Limits**: Enforces hard limits on targeting parameters (e.g., max geo-fences) at the API level to prevent RoaringBitmap memory bloat in the `BiddingEngine`.
- **XSS Prevention**: Implements strict HTML sanitization and Content Security Policy (CSP) headers to prevent malicious code injection via ad creatives.
- **Concurrent Updates**: Utilizes Optimistic Locking (version columns) to handle simultaneous administrative updates to the same campaign.
- **In-Flight Bids**: If a campaign is paused while bids are already submitted to exchanges, the system will still honor win notifications and process billing for bids submitted prior to the pause.

## Security & Compliance
- Robust Authentication and Authorization (JWT / OAuth2) for the Advertiser Portal.
- API Gateways handle rate limiting, TLS, and load shedding before requests reach the `CampaignService` APIs.

## Ecosystem Integration Points
How this service integrates with the broader Food Delivery platform:

- **RestaurantApplication**: `RestaurantApplication` calls `CampaignService` via FeignClient to create campaigns, manage budgets, and fetch analytics (`GET /api/v1/campaigns/restaurant/{restaurantId}`).
- **CommunicationService**: Dispatches Kafka events to `platform.notifications.dispatch` when a campaign budget is running low (< 20% remaining), or is auto-paused due to insufficient funds.
- **ApiGateway**: Public routes for `/api/v1/campaigns/**` are mapped directly to this service to serve the frontend portals.
