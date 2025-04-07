package contracts.buckets

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should list all buckets"
    request {
        method GET()
        url '/buckets'
    }
    response {
        status 200
        body(['mp3', 'resources', 'audio-file']) // Example list of bucket names
        headers {
            contentType(applicationJson())
        }
    }
}
