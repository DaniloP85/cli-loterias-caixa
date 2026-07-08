package br.com.dpsnqmk.controller;

import br.com.dpsnqmk.enums.Loteria;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringParaLoteriaConverter implements Converter<String, Loteria> {

    @Override
    public Loteria convert(String source) {
        return Loteria.from(source);
    }
}
