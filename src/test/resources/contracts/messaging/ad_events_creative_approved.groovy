package contracts.messaging

/*
 * Contract for AD_CREATIVE_APPROVED event emitted by CampaignService.
 * Uses the canonical CampaignChangedEvent schema.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish AD_CREATIVE_APPROVED to ad-events")
    label("ad_events_creative_approved")
    input { triggeredBy('fireAdEventCreativeApproved()') }
    outputMessage {
        sentTo('ad-events')
        headers {
            header('eventType', 'AD_CREATIVE_APPROVED')
            header('aggregateType', 'ADVERTISEMENT')
        }
        body([
            campaignId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            advertiserId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            status: "ACTIVE",
            maxBid: 12.50,
            budget: 500.00,
            budgetExhausted: false
        ])
    }
}
