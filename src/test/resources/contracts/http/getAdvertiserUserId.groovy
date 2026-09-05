import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return advertiser owner userId")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/advertisers/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/owner')), producer('/api/v1/internal/advertisers/123e4567-e89b-12d3-a456-426614174000/owner')))
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            advertiserId: '123e4567-e89b-12d3-a456-426614174000',
            userId: 'user-789'
        ])
    }
}
