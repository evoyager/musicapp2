package contracts.resources

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should download an audio resource"
    request {
        method GET()
        url '/buckets/resources/audio123'
    }
    response {
        status 200
        headers {
            contentType 'audio/mpeg'
        }
        body(fileAsBytes('test.mp3')) // Add a sample audio file in `src/test/resources/contracts` directory
    }
}
