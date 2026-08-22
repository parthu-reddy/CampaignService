package contracts.messaging

/*
 * Contract for AD_CAMPAIGN_COMPLETED event emitted by CampaignService.
 * Uses the canonical CampaignChangedEvent schema.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish AD_CAMPAIGN_COMPLETED to ad-events")
    label("ad_events_completed")
    input { triggeredBy('fireAdEventCompleted()') }
    outputMessage {
        sentTo('ad-events')
        headers {
            header('eventType', 'AD_CAMPAIGN_COMPLETED')
            header('aggregateType', 'ADVERTISEMENT')
        }
        body([
            campaignId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            advertiserId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            status: "COMPLETED",
            maxBid: 12.50,
            budget: 500.00,
            budgetExhausted: false
        ])
    }
}
