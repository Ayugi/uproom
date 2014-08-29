package ru.uproom.prsistence;

import org.springframework.stereotype.Service;
import ru.uproom.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by HEDIN on 10.07.2014.
 */
@SuppressWarnings("unchecked")
@Service
@Transactional
public class UserDaoImpl implements UserDao{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> listUsers() {
        return entityManager.createNamedQuery("findAllUsers").getResultList();
    }

    @Override
    public void saveNewUser(User user) {
        entityManager.persist(user);
    }
}
