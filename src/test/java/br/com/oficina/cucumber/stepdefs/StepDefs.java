package br.com.oficina.cucumber.stepdefs;

import br.com.oficina.OficinaApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = OficinaApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
