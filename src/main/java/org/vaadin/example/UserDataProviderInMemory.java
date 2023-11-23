package org.vaadin.example;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.vaadin.example.model.UserData;
import org.vaadin.example.service.UserDataService;
import org.vaadin.example.service.UserDataServiceImpl;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class UserDataProviderInMemory extends AbstractBackEndDataProvider<UserData, CrudFilter> {


    private static final UserDataService userDataService = new UserDataServiceImpl();
    private Consumer<Long> sizeChangeListener;

    public Map<String, UserData> getMap() {
        return  userDataService.getMap();
    }

    @Override
    protected Stream<UserData> fetchFromBackEnd(Query<UserData, CrudFilter> query) {

        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<UserData> stream = userDataService.getMap().values().stream();

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
        userDataService.persist(item);
    }

    public void editedItem(UserData item) {
        System.out.println("Edit listener called");
//        item.setEditModeFlag(true);

    }

    public void cancelItem(UserData item) {
        System.out.println("cancel item called");
        userDataService.cancelItem(item);
    }

    public void delete(UserData userData) {
       userDataService.delete(userData);
    }
}