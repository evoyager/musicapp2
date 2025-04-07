package contracts.resources

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should upload an audio resource"
    request {
        method POST()
        url '/buckets/resources/audio123'
        headers {
            contentType 'audio/mpeg'
        }
        body(fileAsBytes('test.mp3')) // Add a sample audio file in `src/test/resources/contracts` directory
    }
    response {
        status 200
        body([
                url: "http://localhost:4566/resources/audio123" // Example of resulting URL
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
