package com.shopping.setting;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @Autowired CountryRepository countryRepository;

    @GetMapping("/settings/list_states_by_country/{country_id}")
    private List<StateDTO> listByCountry(@PathVariable("country_id") Integer countryId) {
        List<StateDTO> listStates = new ArrayList<>();
        Country country = countryRepository.findById(countryId).get();

        List<State> states = stateRepository.findByCountryOrderByNameAsc(country);
        for (State state : states) {
            StateDTO stateDTO = new StateDTO(state.getId(), state.getName());
            listStates.add(stateDTO);
        }

        return listStates;
    }
}
