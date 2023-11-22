package org.vaadin.example;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.vaadin.example.model.UserData;
import org.vaadin.example.service.UserServiceImpl;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class UserDataProviderInMemory extends AbstractBackEndDataProvider<UserData, CrudFilter> {

    private static final UserServiceImpl userServiceImpl = new UserServiceImpl();

    private Consumer<Long> sizeChangeListener;

    private final Map<String, Set<String>> editedRecords = new HashMap<>();

    private final List<String> loggedInUser = new ArrayList<>();

    public Map<String, UserData> getMap(){
        return  userServiceImpl.getUserMap();
    }

    @Override
    protected Stream<UserData> fetchFromBackEnd(Query<UserData, CrudFilter> query) {

        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<UserData> stream = userServiceImpl.getUserMap().values().stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<UserData, CrudFilter> query) {
        // For RDBMS just execute a SELECT COUNT(*) ... WHERE query
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private static Predicate<UserData> predicate(CrudFilter filter) {
        // For RDBMS just generate a WHERE clause
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<UserData>) UserData -> {
                    try {
                        Object value = valueOf(constraint.getKey(), UserData);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<UserData> comparator(CrudFilter filter) {
        // For RDBMS just generate an ORDER BY clause
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<UserData> comparator = Comparator.comparing(
                        UserData -> (Comparable) valueOf(sortClause.getKey(),
                                UserData));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<UserData>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, UserData UserData) {
        try {
            Field field = UserData.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(UserData);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void persist(UserData item) {
        String uuid;

        if (item.getUserId() == null) {
            uuid = UUID.randomUUID().toString();
            item.setUserId(uuid);
            userServiceImpl.getUserMap().put(uuid, item);
        } else {
            Optional<UserData> userData = find(item.getUserId());
            if(userData.isPresent()) {

                if(userData.get().getVersion().equals(item.getVersion())) {
                    item.setVersion(item.getVersion()+1);
                    userServiceImpl.getUserMap().replace(userData.get().getUserId(), item);
                } else {
                    ConfirmDialog dialog = new ConfirmDialog();
                    dialog.setHeader("Export failed");
                    dialog.setText(new Html(
                            "<p>This data has already modified from another user" +
                                    "</p>"));

                    dialog.setConfirmText("OK");
                    dialog.open();

                }

            }
        }
    }



    Optional<UserData> find(String userId) {
        return Optional.of( userServiceImpl.getUserMap().get(userId));
    }

    public void delete(UserData userData) {
        if(userServiceImpl.getUserMap().remove(userData.getUserId()) == null) {
            Notification.show("This user has alread deleted");
        }
    }

    public Boolean isUserAlreadyExist(String userToken){
        return editedRecords.containsKey(userToken);
    }
}