package com.gamerstore.cart_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItemDTO implements Serializable {

    private Long productId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    private String imageUrl;

    private Integer estoque;

    public CartItemDTO() {
    }

    public CartItemDTO(Long productId, String nome, String descricao, BigDecimal preco, Integer quantidade,
            String imageUrl, Integer estoque) {
        this.productId = productId;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
        this.imageUrl = imageUrl;
        this.estoque = estoque;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }
}