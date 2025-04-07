package contracts.buckets

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should delete a bucket"
    request {
        method DELETE()
        url '/buckets/test-bucket' // Example bucket name
    }
    response {
        status 200
    }
}
