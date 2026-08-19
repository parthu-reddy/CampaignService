package contracts.messaging

/*
 * ad-events / AD_CAMPAIGN_PAUSED, from CampaignServiceImpl:80 (pauseCampaign). Same flat Campaign
 * payload as ad_events, different event type -- which matters: BiddingEngine removes the campaign
 * from the matcher on this, and CommunicationIntegration's AdNotificationListener only reacts to
 * AD_CAMPAIGN_PAUSED and AD_BUDGET_ALERT, so AD_CAMPAIGN_CREATED alone cannot exercise it.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish the paused Campaign to ad-events")
    label("ad_events_paused")
    input { triggeredBy('fireAdCampaignPaused()') }
    outputMessage {
        sentTo('ad-events')
        headers { header('eventType', 'AD_CAMPAIGN_PAUSED') }
        body([
            id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            advertiserId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            name: "Summer Pizza Push",
            status: "PAUSED",
            dailyBudget: 500.00,
            maxBid: 12.50
        ])
    }
}
