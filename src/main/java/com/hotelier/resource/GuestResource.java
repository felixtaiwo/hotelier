package com.hotelier.resource;


import com.hotelier.exception.Hotelier;
import com.hotelier.service.GuestService;
import com.hotelier.model.dto.GuestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Constants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.BindException;
import java.util.List;
import java.util.logging.Logger;


@RestController @RequiredArgsConstructor
@RequestMapping("api/v1/guest") @Transactional(rollbackFor = {Hotelier.class, NullPointerException.class, Exception.class, BindException.class, Constants.ConstantException.class})
@Tag(name="Guest", description = "guest endpoints")
public class GuestResource {
    Logger log = Logger.getLogger(this.getClass().getName());
    private final GuestService guestService;

    @GetMapping
    public List<GuestDto> getGuests(){
        return guestService.getAllGuest();
    }
    @PostMapping
    public void addOrEditGuest(@RequestBody @Valid GuestDto request, @RequestParam(required = false) Long userId){
        guestService.addOrEditGuest(request, userId);
    }

}
