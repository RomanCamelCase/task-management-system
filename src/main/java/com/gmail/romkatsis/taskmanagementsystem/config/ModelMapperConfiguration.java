package com.gmail.romkatsis.taskmanagementsystem.config;

import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TaskResponse;
import com.gmail.romkatsis.taskmanagementsystem.models.Task;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        convertTaskToTaskResponse(modelMapper);
        return modelMapper;
    }

    private void convertTaskToTaskResponse(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Task.class, TaskResponse.class)
                .addMapping(task -> task.getOwner().getId(), TaskResponse::setOwnerId);
    }
}
