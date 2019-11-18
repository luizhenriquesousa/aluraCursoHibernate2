package br.com.caelum.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		return em.createQuery("from Produto", Produto.class).getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	/**
	 * @author lhsousa CriteriaBuilder utilizado para criação/Construção das querys,
	 *         possui métodos de operação de buscas, "selects". Create Query é o
	 *         retorno dessa consulta, tenho que colocar no parâmetro a classe que é
	 *         minha tabela.
	 * @param nome
	 * @param categoriaId
	 * @param lojaId
	 * @return
	 * @Transaction há um EntityManager ativo
	 */
	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		//Session é um forma de criar uma EntityManager no hibernate2
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		//CriteriaQuery no hibernate2 é equivalente a Criteria
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);

		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> lojaPath = root.<Loja>get("loja").<Integer>get("id");
		//SetFetchMode é equivalente a um Join que é criado dentro do meu if da para fazer direto
		//get sem ponto é do tipo genérico get com ponto é o get comum em ambas funciona
		Path<Integer> categoriaPath = root.join("categorias").<Integer>get("id");

		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			//Restrictions é a forma de fazer um Criteria builder no Hibernate2 filtros de busca
			Predicate nomeIgual = criteriaBuilder.like(nomePath, nome);
			predicates.add(nomeIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaPath, categoriaId);
			predicates.add(categoriaIgual);
		}
		if (lojaId != null) {
			Predicate lojaIgual = criteriaBuilder.equal(lojaPath, lojaId);
			predicates.add(lojaIgual);
		}

		query.where((Predicate[]) predicates.toArray(new Predicate[0]));

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

	public List<Produto> getConsultaSimples() {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);

		// Ele faz a função de um From
		Root<Produto> root = query.from(Produto.class);

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public List<Produto> getProdutosTeste(String nome, Integer categoriaId, Integer lojaId) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		// Ele faz a mesma função de um from
		Root<Produto> root = query.from(Produto.class);

		// Para garantir o retorno e criar mais de uma condição o nosso código.
		// Para cada tipo de busca de um determinado campo, devo criar um path.
		// E minha List é de acordo com meu tipo para cada variável.
		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> categoriaIdPath = root.<Loja>get("Loja").<Integer>get("id");
		// Fazendo join a partir do produto
		Path<Integer> lojaIdPath = root.join("categorias").<Integer>get("id");

		// Guardando os predicates, para criar conectivo and, or, conjuction e disjuction e passar predicates para
		// cláusula Where
		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			// Retornar um Predicate que é um método de igualdade junto ao equal
			Predicate nomeIgual = criteriaBuilder.like(nomePath, "%" + nome + "%");
			predicates.add(nomeIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaIdPath, categoriaId);
			predicates.add(categoriaIgual);
		}
		if (lojaId != null) {
			Predicate lojaIgual = criteriaBuilder.equal(lojaIdPath, lojaId);
			predicates.add(lojaIgual);
		}

		// Convertendo a lista em um array, passando para método where.
		query.where((Predicate[]) predicates.toArray(new Predicate[0]));

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		// Resultado da minha consulta.
		return typedQuery.getResultList();
	}

}
