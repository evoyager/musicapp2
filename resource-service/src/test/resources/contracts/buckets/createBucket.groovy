package contracts.buckets

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create a new bucket"
    request {
        method POST()
        url '/buckets/test-bucket' // Example bucket name
    }
    response {
        status 200
    }
}
