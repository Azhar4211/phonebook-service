package org.vaadin.example;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;
import org.vaadin.example.model.UserData;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;


@Route("")
public class MainView extends VerticalLayout {

    private Crud<UserData> crud;
    private String FIRST_NAME = "name";
    private String LAST_NAME = "lastName";
    private String EMAIL = "email";

    private UserData currentUser;

    private final Set<String> uniquePhoneNumbers = new ConcurrentSkipListSet<>();

    UserDataProviderInMemory dataProvider = new UserDataProviderInMemory();


    public MainView() {

        crud = new Crud<>(UserData.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupToolbar();
        add(crud);

    }

    private void setupGrid() {
        Grid<UserData> grid = crud.getGrid();


        Crud.removeEditColumn(grid);
        grid.addItemDoubleClickListener(event -> {
            currentUser = event.getItem();
            crud.edit(event.getItem(), Crud.EditMode.EXISTING_ITEM);

            UI ui = UI.getCurrent();

            // Get the client ID
            String clientId = String.valueOf(ui.getId());

            // Get the CSRF token
            String csrfToken = ui.getCsrfToken();

            // Log or use the client ID and CSRF token as needed
            System.out.println("Client ID: " + clientId);
            System.out.println("CSRF Token: " + csrfToken);

        });



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

        crud.setDataProvider(dataProvider);

        crud.addDeleteListener(
                deleteEvent -> dataProvider.delete(deleteEvent.getItem()));

        crud.addSaveListener(
                saveEvent -> dataProvider.persist(saveEvent.getItem()));

    }

    private CrudEditor<UserData> createEditor() {
        TextField name = new TextField("First name");
        TextField lastName = new TextField("Last name");
        TextField street = new TextField("Street");
        TextField city = new TextField("City");
        TextField country = new TextField("Country");
        TextField phoneNumber = new TextField("Phone Number");
        EmailField email = new EmailField("Email");

        FormLayout form = new FormLayout(name, lastName, street, city, country, phoneNumber, email);


        form.setColspan(email, 2);
        form.setMaxWidth("480px");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("30em", 2));

        Binder<UserData> binder = new Binder<>(UserData.class);

        binder.forField(name).asRequired().bind(UserData::getName,
                UserData::setName);
        binder.forField(lastName).asRequired().bind(UserData::getLastName,
                UserData::setLastName);
        binder.forField(street).asRequired().bind(UserData::getStreet,
                UserData::setStreet);
        binder.forField(city).asRequired().bind(UserData::getCity,
                UserData::setCity);
        binder.forField(country).asRequired().bind(UserData::getCountry,
                UserData::setCountry);

        binder.forField(email).withValidator(new EmailValidator("Not a valid email")).asRequired().bind(UserData::getEmail,
                UserData::setEmail);


        binder.forField(phoneNumber)
                .withValidator(this::isPhoneNumberUnique, "Phone number must be unique")
                .asRequired().bind(UserData::getPhoneNumber,
                        UserData::setPhoneNumber);

        return new BinderCrudEditor<>(binder, form);
    }

    private boolean isPhoneNumberUnique(String phoneNumber) {

        return dataProvider.DATABASE.values().stream()
            .noneMatch(userData -> userData.getPhoneNumber().equals(phoneNumber)
                    && !userData.getPhoneNumber().equalsIgnoreCase(currentUser.getPhoneNumber()));

        //return uniquePhoneNumbers.add(phoneNumber);
    }

    private void setupToolbar() {
        Html total = new Html("<span>Total: <b>" + dataProvider.DATABASE.values().size()
                + "</b> Users</span>");

        Button button = new Button("New User", VaadinIcon.PLUS.create());

        button.addClickListener(event -> {
            crud.edit(new UserData(), Crud.EditMode.NEW_ITEM);
        });

        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        crud.setNewButton(button);

        HorizontalLayout toolbar = new HorizontalLayout(total);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setFlexGrow(1, toolbar);
        toolbar.setSpacing(false);
        crud.setToolbar(toolbar);
    }
}
