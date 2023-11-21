package org.vaadin.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.vaadin.example.model.UserData;
import org.vaadin.example.service.UserService;

import static java.util.Comparator.naturalOrder;

// PhoneBook data provider
public class UserDataProvider extends AbstractBackEndDataProvider<UserData, CrudFilter> {

    // A real app should hook up something like JPA
//    final List<PhoneBook> DATABASE = new ArrayList<>(DataService.getPeople());


    final List<UserData> DATABASE = new ArrayList<>(UserService.getAllUsers());

    private Consumer<Long> sizeChangeListener;



    @Override
    protected Stream<UserData> fetchFromBackEnd(Query<UserData, CrudFilter> query) {

        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<UserData> stream = DATABASE.stream();

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

    void persist(UserData item) {
        if (item.getId() == null) {
            item.setId(DATABASE.stream().map(UserData::getId).max(naturalOrder())
                    .orElse(0) + 1);
        }

        final Optional<UserData> existingItem = find(item.getId());
        if (existingItem.isPresent()) {
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            DATABASE.add(position, item);
        } else {
            DATABASE.add(item);
        }
    }

    Optional<UserData> find(Integer id) {
        return DATABASE.stream().filter(entity -> entity.getId().equals(id))
                .findFirst();
    }

    void delete(UserData item) {
        DATABASE.removeIf(entity -> entity.getId().equals(item.getId()));
    }
}