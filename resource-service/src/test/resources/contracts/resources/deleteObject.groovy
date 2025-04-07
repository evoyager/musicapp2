package contracts.resources

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should delete an object from the bucket"
    request {
        method DELETE()
        url '/buckets/resources/objects/audio123'
    }
    response {
        status 200
    }
}
