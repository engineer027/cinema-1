package com.dev.cinema.dao.impl;

import com.dev.cinema.dao.MovieSessionDao;
import com.dev.cinema.exception.DataProcessingException;
import com.dev.cinema.lib.Dao;
import com.dev.cinema.model.MovieSession;
import com.dev.cinema.util.HibernateUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class MovieSessionDaoImpl implements MovieSessionDao {
    @Override
    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MovieSession> sessionQuery = session.createQuery("from MovieSession "
                            + "where id = :movieId and DATE_FORMAT(showTime,'%Y-%m-%d') "
                            + "= :date", MovieSession.class);
            sessionQuery.setParameter("movieId", movieId);
            sessionQuery.setParameter("date", date.format(DateTimeFormatter.ISO_DATE));
            return sessionQuery.getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find available session from id "
                    + movieId + " and Local date time: " + date, e);
        }
    }

    @Override
    public MovieSession add(MovieSession session) {
        Session currentSession = null;
        Transaction transaction = null;
        try {
            currentSession = HibernateUtil.getSessionFactory().openSession();
            transaction = currentSession.beginTransaction();
            currentSession.save(session);
            transaction.commit();
            return session;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't create movie session " + session, e);
        } finally {
            if (currentSession != null) {
                currentSession.close();
            }
        }
    }
}
