package contracts.http

/*
 * Contract for POST /api/v1/internal/campaigns/batch/budgets.
 *
 * This is the only synchronous cross-service call in the ad platform:
 * BudgetLimitingService's CampaignClient calls it to pace spend against each
 * campaign's daily and lifetime budget. Numeric values are written as Doubles
 * because CampaignPacingDTO holds Double, so Jackson emits 500.0 rather than 500.00.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should return daily and lifetime budgets keyed by campaign id")
    request {
        method 'POST'
        url '/api/v1/internal/campaigns/batch/budgets'
        headers {
            contentType(applicationJson())
        }
        body(["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"])
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
            "11111111-1111-1111-1111-111111111111": [
                dailyBudget   : 500.0,
                lifetimeBudget: 5000.0,
                advertiserId  : "550e8400-e29b-41d4-a716-446655440000"
            ],
            "22222222-2222-2222-2222-222222222222": [
                dailyBudget   : 1500.5,
                lifetimeBudget: 15000.5,
                advertiserId  : "550e8400-e29b-41d4-a716-446655440001"
            ]
        ])
    }
}
