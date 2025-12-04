package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.DolarApiResponse;
import com.ryfsystems.ryftaxi.service.DolarApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class DolarApiServiceImpl implements DolarApiService {

    private final RestTemplate restTemplate;

    @Value("${app.dolar-api.url}")
    private String dolarApiUrl;

    @Override
    public DolarApiResponse getOfficialRate() {
        try {
            log.info("getOfficialRate desde: {}", dolarApiUrl);

            String url = UriComponentsBuilder
                    .fromUriString(dolarApiUrl)
                    .path("/oficial")
                    .build()
                    .toUriString();

            ResponseEntity<DolarApiResponse> response = restTemplate.getForEntity(url, DolarApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DolarApiResponse apiResponse = response.getBody();
                log.info("Tasa obtenida: Promedio: {}", apiResponse.getPromedio());

                return apiResponse;
            } else {
                log.error("Error al obtener tasa oficial. Código: {}", response.getStatusCode());
                throw new RuntimeException("No se pudo obtener la tasa oficial del dólar");
            }
        } catch (Exception e) {
            log.error("Error al consumir API de DolarAPI: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener la tasa del dólar: " + e.getMessage());
        }
    }
}
