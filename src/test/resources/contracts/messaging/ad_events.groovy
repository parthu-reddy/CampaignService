package contracts.messaging

/*
 * Real wire payload for ad-events, from CampaignServiceImpl.publishOutboxEvent:
 * the saved Campaign entity serialized directly (ADVERTISEMENT aggregate, key = campaign id,
 * eventType AD_CAMPAIGN_CREATED / _UPDATED / _PAUSED).
 *
 * RELOCATED: this contract previously lived in CustomerApplication, which never publishes to
 * ad-events. CampaignService is the only producer of the ADVERTISEMENT aggregate.
 *
 * KNOWN CONSUMER DEFECT (not encoded here on purpose): BiddingEngine's CampaignEventConsumer is
 * guarded by  if (root.has("eventType") && root.has("payload"))  and reads payload.get("id").
 * This payload is a FLAT Campaign with neither key, so the guard is never true and campaigns are
 * never indexed into or removed from the matcher. This contract describes the producer truthfully.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish the saved Campaign to ad-events")
    label("ad_events")
    input { triggeredBy('fireAdEvent()') }
    outputMessage {
        sentTo('ad-events')
        headers { header('eventType', 'AD_CAMPAIGN_CREATED') }
        body([
            id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            advertiserId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            name: "Summer Pizza Push",
            status: "ACTIVE",
            dailyBudget: 500.00,
            maxBid: 12.50
        ])
    }
}
