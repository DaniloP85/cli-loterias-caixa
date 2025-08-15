package br.com.dpsnqmk;

import br.com.dpsnqmk.dto.DataJsonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonWriter {

    public static void run(List<DataJsonDTO> data, String loteria) {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(String.format("%s.json", loteria));

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}