package org.vaadin.example;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.vaadin.example.model.UserData;

/**
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {


        var crud = new Crud<>(UserData.class, createEditor());

        add(crud);







/*
        // Use TextField for standard text input
        TextField textField = new TextField("Your name");
        textField.addClassName("bordered");
        // Button click listeners can be defined as lambda expressions
        GreetService greetService = new GreetService();
        Button button = new Button("Say hello", e -> {
            add(new Paragraph(greetService.greet(textField.getValue())));
        });

        // Theme variants give you predefined extra styles for components.
        // Example: Primary button is more prominent look.
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // You can specify keyboard shortcuts for buttons.
        // Example: Pressing enter in this view clicks the Button.
        button.addClickShortcut(Key.ENTER);

        // Use custom CSS classes to apply styling. This is defined in
        // styles.css.
        addClassName("centered-content");

        add(textField, button);

    */
    }

    private Grid<UserData> createGrid() {
        Grid<UserData> grid = new Grid<>();
        Crud.addEditColumn(grid);
        grid.addColumn(UserData::getName).setHeader("First name");
        grid.addColumn(UserData::getEmail).setHeader("Email");

        return grid;
    }

    private CrudEditor<UserData> createEditor() {
        TextField name = new TextField("Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");

        FormLayout form = new FormLayout(name);
        form.setColspan(email, 2);
        form.setMaxWidth("480px");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("30em", 2));

        Binder<UserData> binder = new Binder<>(UserData.class);
        binder.forField(name).asRequired().bind(UserData::getName,
                UserData::setName);
        binder.forField(email).asRequired().bind(UserData::getEmail,
                UserData::setEmail);
        return new BinderCrudEditor<>(binder, form);
    }
}
