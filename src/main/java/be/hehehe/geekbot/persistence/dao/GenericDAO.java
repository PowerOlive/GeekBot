package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.EntityManagerProducer;

public abstract class GenericDAO<T> {

	protected CriteriaBuilder builder;
	protected EntityManager em;

	@AroundInvoke
	public Object initEntityManager(InvocationContext ctx) throws Exception {
		Object result = null;
		try {
			em = EntityManagerProducer.createEntityManager();
			em.getTransaction().begin();

			builder = em.getCriteriaBuilder();
			result = ctx.proceed();

			em.getTransaction().commit();
		} catch (Exception e) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().setRollbackOnly();
			}
			throw e;
		} finally {
			if (em != null) {
				em.close();
			}
		}
		return result;
	}

	public void save(T object) {
		em.persist(object);
	}

	public void update(T... objects) {
		for (Object object : objects) {
			em.merge(object);
		}
	}

	public void delete(T object) {
		object = em.merge(object);
		em.remove(object);
	}

	public void deleteById(Object id) {
		Object ref = em.getReference(getType(), id);
		em.remove(ref);
	}

	public T findById(long id) {
		T t = em.find(getType(), id);
		return t;
	}

	public List<T> findAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(getType());
		query.from(getType());
		return em.createQuery(query).getResultList();
	}

	public List<T> findAll(int startIndex, int count) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(getType());
		query.from(getType());
		TypedQuery<T> q = em.createQuery(query);
		q.setMaxResults(count);
		q.setFirstResult(startIndex);
		return q.getResultList();

	}

	public long getCount() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(getType());
		query.select(builder.count(root));
		return em.createQuery(query).getSingleResult();
	}

	public <Y> List<T> findByField(String field, Object value) {
		CriteriaQuery<T> query = builder.createQuery(getType());
		Root<T> root = query.from(getType());
		query.where(builder.equal(root.get(field), value));
		return em.createQuery(query).getResultList();
	}

	protected abstract Class<T> getType();

}
