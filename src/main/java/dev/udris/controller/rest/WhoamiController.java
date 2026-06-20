package dev.udris.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoamiController {

    @GetMapping("/whoami")
    public String whoami() {
        return System.getenv().getOrDefault("HOSTNAME", "unknown");
    }
}
