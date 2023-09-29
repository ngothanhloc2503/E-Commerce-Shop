package com.shopping.admin.setting.controller;

import com.shopping.common.entity.StateDTO;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.setting.repository.StateRepository;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @Autowired CountryRepository countryRepository;

    @GetMapping("/states/list_by_country/{country_id}")
    private List<StateDTO> listByCountry(@PathVariable("country_id") Integer countryId) {
        Country country = countryRepository.findById(countryId).get();

        List<StateDTO> listStates = new ArrayList<>();
        List<State> states = stateRepository.findByCountryOrderByNameAsc(country);
        for (State state : states) {
            StateDTO stateDTO = new StateDTO(state.getId(), state.getName());
            listStates.add(stateDTO);
        }

        return listStates;
    }

    @PostMapping("/states/save")
    private String saveState(@RequestBody State state) {
        return String.valueOf(stateRepository.save(state).getId());
    }

    @DeleteMapping("/states/delete/{state_id}")
    private void deleteState(@PathVariable("state_id") Integer id) {
        stateRepository.deleteById(id);
    }
}
