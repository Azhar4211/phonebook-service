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

import java.util.Arrays;
import java.util.List;

/**
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route("")
public class MainView extends VerticalLayout {

    private Crud<UserData> crud;

    private String FIRST_NAME = "name";
    private String LAST_NAME = "lastName";
    private String EMAIL = "email";



    public MainView() {


        crud = new Crud<>(UserData.class, createEditor());
        setupDataProvider();
        setupGrid();


        add(crud);

    }

    private void setupGrid() {
        Grid<UserData> grid = crud.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(FIRST_NAME, LAST_NAME, EMAIL);
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        // Reorder the columns (alphabetical by default)
        grid.setColumnOrder(grid.getColumnByKey(FIRST_NAME),
                grid.getColumnByKey(LAST_NAME),
                grid.getColumnByKey(EMAIL));
    }

    private void setupDataProvider() {
        UserDataProvider dataProvider = new UserDataProvider();
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(
                deleteEvent -> dataProvider.delete(deleteEvent.getItem()));
        crud.addSaveListener(
                saveEvent -> dataProvider.persist(saveEvent.getItem()));
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
