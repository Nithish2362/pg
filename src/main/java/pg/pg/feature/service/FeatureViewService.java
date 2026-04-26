package pg.pg.feature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.feature.model.Features;
import pg.pg.feature.repository.FeaturesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureViewService {

    private final FeaturesRepository featuresRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:view.json")
    private Resource viewJsonResource;

    @PostConstruct
    public void initializeDefaultViews() {
        if (featuresRepository.count() == 0) {
            saveViewsFromFile();
        }
    }

    public List<Features> getAllFeatures() {
        List<Features> features = featuresRepository.findAll();
        if (features.isEmpty()) {
            return resetFromViewFile();
        }
        return sortByOrder(features);
    }

    @Transactional
    public List<Features> replaceViews(List<Features> views) {
        featuresRepository.deleteAll();
        if (views == null || views.isEmpty()) {
            return saveViewsFromFile();
        }
        return sortByOrder(featuresRepository.saveAll(views));
    }

    @Transactional
    public List<Features> resetFromViewFile() {
        featuresRepository.deleteAll();
        return saveViewsFromFile();
    }

    private List<Features> saveViewsFromFile() {
        List<Features> defaultViews = readViewsFromFile();
        if (defaultViews.isEmpty()) {
            return defaultViews;
        }
        return sortByOrder(featuresRepository.saveAll(defaultViews));
    }

    private List<Features> readViewsFromFile() {
        try (InputStream inputStream = viewJsonResource.getInputStream()) {
            Features[] features = objectMapper.readValue(inputStream, Features[].class);
            return List.of(features);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read default views from view.json", ex);
        }
    }

    private List<Features> sortByOrder(List<Features> features) {
        return features.stream()
                .sorted(Comparator.comparing(Features::getOrderBy, Comparator.nullsLast(Integer::compareTo)))
                .toList();
    }
}
