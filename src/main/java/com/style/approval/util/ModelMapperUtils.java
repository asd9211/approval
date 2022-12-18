package com.style.approval.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperUtils {
    private ModelMapperUtils (){}
    private static ModelMapper modelMapper = new ModelMapper();

    public static ModelMapper getModelMapper() {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return source
          .stream()
          .map(element -> modelMapper.map(element, targetClass))
          .collect(Collectors.toList());
    }
}
