package contracts.messaging

/*
 * Contract for AD_CAMPAIGN_PAUSED event emitted by CampaignService.
 * Uses the canonical CampaignChangedEvent schema.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish AD_CAMPAIGN_PAUSED to ad-events")
    label("ad_events_paused")
    input { triggeredBy('fireAdEventPaused()') }
    outputMessage {
        sentTo('ad-events')
        headers {
            header('eventType', 'AD_CAMPAIGN_PAUSED')
            header('aggregateType', 'ADVERTISEMENT')
        }
        body([
            campaignId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            advertiserId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            status: "PAUSED",
            maxBid: 12.50,
            budget: 500.00,
            budgetExhausted: false
        ])
    }
}
