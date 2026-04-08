package com.nourane.shopflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nourane.shopflow.dto.dashboard.DashboardDTOs.*;
import com.nourane.shopflow.entity.Order;
import com.nourane.shopflow.entity.User;
import com.nourane.shopflow.entity.enums.OrderStatus;
import com.nourane.shopflow.exception.ResourceNotFoundException;
import com.nourane.shopflow.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminDashboard getAdminDashboard() {
        AdminDashboard dashboard = new AdminDashboard();
        dashboard.setChiffreAffairesGlobal(orderRepository.calculateChiffreAffairesGlobal());
        dashboard.setTotalCommandes(orderRepository.count());
        dashboard.setCommandesEnAttente(orderRepository.countByStatut(OrderStatus.PENDING));
        dashboard.setTotalUtilisateurs(userRepository.count());
        dashboard.setTotalProduits(productRepository.count());

        // Top produits
        dashboard.setTopProduits(
            productRepository.findTop10BySales(PageRequest.of(0, 5)).stream().map(p -> {
                TopProduit tp = new TopProduit();
                tp.setId(p.getId());
                tp.setNom(p.getNom());
                tp.setTotalVentes(p.getTotalVentes());
                tp.setImageUrl(p.getImages().isEmpty() ? null : p.getImages().get(0));
                return tp;
            }).collect(Collectors.toList())
        );

        // Commandes récentes (dernières 48h)
        dashboard.setCommandesRecentes(
            orderRepository.findCommandesRecentes(LocalDateTime.now().minusHours(48))
                .stream().limit(10).map(this::toCommandeRecente).collect(Collectors.toList())
        );

        return dashboard;
    }

    public SellerDashboard getSellerDashboard(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur introuvable"));

        SellerDashboard dashboard = new SellerDashboard();
        dashboard.setRevenus(orderRepository.calculateRevenuVendeur(seller.getId()));
        dashboard.setCommandesEnAttente(
            orderRepository.findCommandesEnAttentePourVendeur(seller.getId()).size()
        );
        dashboard.setTotalProduits(
            productRepository.findBySellerId(seller.getId(), PageRequest.of(0, 1)).getTotalElements()
        );

        // Alertes stock faible (seuil = 5)
        dashboard.setAlertesStockFaible(
            productRepository.findStockFaible(5, seller.getId()).stream().map(p -> {
                StockFaible sf = new StockFaible();
                sf.setProductId(p.getId());
                sf.setNom(p.getNom());
                sf.setStock(p.getStock());
                return sf;
            }).collect(Collectors.toList())
        );

        dashboard.setCommandesRecentes(
            orderRepository.findCommandesEnAttentePourVendeur(seller.getId())
                .stream().limit(5).map(this::toCommandeRecente).collect(Collectors.toList())
        );

        return dashboard;
    }

    public CustomerDashboard getCustomerDashboard(String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        CustomerDashboard dashboard = new CustomerDashboard();
        List<Order> orders = orderRepository.findByCustomerId(customer.getId(), PageRequest.of(0, 100)).getContent();
        dashboard.setTotalCommandes(orders.size());
        dashboard.setCommandesEnCours(
            orders.stream().filter(o -> o.getStatut() != OrderStatus.DELIVERED
                    && o.getStatut() != OrderStatus.CANCELLED
                    && o.getStatut() != OrderStatus.REFUNDED).count()
        );
        dashboard.setMesCommandes(orders.stream().limit(5).map(this::toCommandeRecente).collect(Collectors.toList()));
        return dashboard;
    }

    private CommandeRecente toCommandeRecente(Order order) {
        CommandeRecente cr = new CommandeRecente();
        cr.setId(order.getId());
        cr.setNumeroCommande(order.getNumeroCommande());
        cr.setStatut(order.getStatut().name());
        cr.setClientNom(order.getCustomer().getPrenom() + " " + order.getCustomer().getNom());
        cr.setTotalTTC(order.getTotalTTC());
        cr.setDateCommande(order.getDateCommande());
        return cr;
    }
}