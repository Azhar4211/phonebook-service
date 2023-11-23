package org.vaadin.example;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;
import org.vaadin.example.model.UserData;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Route("")
public class MainView extends VerticalLayout {

    private Crud<UserData> crud;
    private String FIRST_NAME = "name";
    private String LAST_NAME = "lastName";
    private String EMAIL = "email";

    private UserData currentUser;

    private final UserDataProviderInMemory dataProvider = new UserDataProviderInMemory();


    public MainView() {
        crud = new Crud<>(UserData.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupToolbar();
        add(new H1("Phone Book Application"));
        add(crud);

    }

    private void setupGrid() {
        Grid<UserData> grid = crud.getGrid();

        Crud.removeEditColumn(grid);

        grid.addItemDoubleClickListener(event -> {
            cocurrentUserHandle(event.getItem());
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

    private void cocurrentUserHandle(UserData item) {

        System.out.println("Double click called");
        UserData cloned = item.clone();
        currentUser = cloned;

        List<UserData> userDataList = crud.getGrid().getDataProvider().fetch(new Query<>()).toList();
        Optional<UserData> existingRecord = userDataList.stream().filter(user-> user.getUserId().equalsIgnoreCase(item.getUserId())).findFirst();

        if(!item.isEditModeFlag()) {
            item.setEditModeFlag(true);
            crud.edit(item, Crud.EditMode.EXISTING_ITEM);


        } else {
            showWarningNotification();
            crud.edit(item, Crud.EditMode.EXISTING_ITEM);
        }

    }
    private void showWarningNotification(){
        Div text = new Div(
                new Text("This Data is being modified by another user"),
                new HtmlComponent("br"));

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event2 -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();

    }

    private void setupDataProvider() {

        crud.setDataProvider(dataProvider);

        crud.addDeleteListener(
                deleteEvent -> dataProvider.delete(deleteEvent.getItem()));

        crud.addSaveListener(
                saveEvent -> dataProvider.persist(saveEvent.getItem()));

        crud.addEditListener(userDataEditEvent -> dataProvider.editedItem(userDataEditEvent.getItem()));

        crud.addCancelListener(cancelEvent -> dataProvider.cancelItem(cancelEvent.getItem()));
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

        return dataProvider.getMap().values().stream()
            .noneMatch(userData -> userData.getPhoneNumber().equals(phoneNumber)
                    && !userData.getPhoneNumber().equalsIgnoreCase(currentUser.getPhoneNumber()));
    }

    private void setupToolbar() {
        Html total = new Html("<span>Total: <b>" + dataProvider.getMap().values().size()
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
