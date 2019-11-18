package br.com.caelum.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Produto;

/**
 * 
 * @author lhsousa Forma em JPQL
 */
@Repository
public class TesteProdutoDao {

	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @author lhsousa É possível utilizar porém não é recomedável, para uma
	 *         manutenção acaba gerando muito retrabalho. É mais recomendável
	 *         utilizar o modo Criteria. Porém fica a escolha do Desenvolvedor e do
	 *         time de arquitetura de software.
	 */
	public List<Produto> getJpql(String nome, Integer categoriaId, Integer lojaId) {

		String jpql = "Select p from produto p";

		if (categoriaId != null) {
			jpql += "join fetch p.categorias c where c.id = :pCategoria and ";
		} else {
			jpql += "where ";
		}
		if (lojaId != null) {
			jpql += "p.loja.id = :pLoja and ";
		}
		if (!nome.isEmpty()) {
			jpql += "p.nome like :pNome and ";
		}

		jpql += "1=1";

		TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);

		if (categoriaId != null) {
			query.setParameter("pCategoria", categoriaId);
		}
		if (lojaId != null) {
			query.setParameter("pLoja", lojaId);
		}
		if (!nome.isEmpty()) {
			// Like em JPQL
			query.setParameter("pNome", "%" + nome + "%");
		}

		List<Produto> resultSqlProduto = query.getResultList();

		return resultSqlProduto;
	}

}
