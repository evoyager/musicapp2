package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.client.ResourceServiceClient;
import com.epam.resourceprocessor.client.SongServiceClient;
import com.epam.resourceprocessor.dto.ResourceDto;
import com.epam.resourceprocessor.messaging.producer.RabbitMQProducer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProcessorService {

    private final Tracer tracer;
    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final RabbitMQProducer rabbitMQProducer;

    public void getAudioDataExtractMetadataAndSendToSongService(ResourceDto resource) {
        Span currentSpan = tracer.currentSpan();

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(currentSpan)) {
            String traceId = currentSpan.context().traceId();
            String spanId = currentSpan.context().spanId();

            log.info("Trace ID in ResourceProcessorService: [{}], Span ID: [{}]", traceId, spanId);

            String resourceId = resource.getResourceId();
            byte[] audioData = resourceServiceClient.fetchAudioFile(resourceId);

            Map<String, String> metadata = extractMetadata(audioData);

            songServiceClient.createSongMetadata(metadata, resource);
            rabbitMQProducer.indicateResourceHasBeenProcessed(resource);
        }
    }

    public Map<String, String> extractMetadata(byte[] data) {
        Map<String, String> metadataMap = new HashMap<>();
        Mp3Parser parser = new Mp3Parser();

        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            parser.parse(inputStream, handler, metadata, new ParseContext());

            String[] metadataNames = metadata.names();
            for (String name : metadataNames) {
                metadataMap.put(name, metadata.get(name));
            }
        } catch (IOException | SAXException | org.apache.tika.exception.TikaException e) {
            log.error("An error occurred during MP3 metadata extraction: {}", e.getMessage(), e);
        }

        return metadataMap;
    }

}
