-- Mots de passe : "password123" (BCrypt hash)
INSERT INTO users (email, mot_de_passe, prenom, nom, role, actif, date_creation) VALUES
                                                                                     ('admin@shopflow.tn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Super', 'Admin', 'ADMIN', true, NOW()),
                                                                                     ('seller@shopflow.tn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Karim', 'Trabelsi', 'SELLER', true, NOW()),
                                                                                     ('client@shopflow.tn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amira', 'Mansour', 'CUSTOMER', true, NOW());

INSERT INTO seller_profile (user_id, nom_boutique, description, note) VALUES (2, 'Artisanat Karim', 'Créations artisanales tunisiennes', 4.8);
INSERT INTO cart (customer_id, date_modification) VALUES (3, NOW());
INSERT INTO address (user_id, rue, ville, code_postal, pays, principal) VALUES (3, '12 Av. Bourguiba', 'Tunis', '1000', 'Tunisie', true);

INSERT INTO category (nom, description) VALUES ('Sacs', 'Sacs à main'), ('Bijoux', 'Bijoux artisanaux'), ('Vêtements', 'Vêtements traditionnels');

INSERT INTO products (seller_id, nom, description, prix, prix_promo, stock, actif, date_creation, total_ventes) VALUES
                                                                                                                    (2, 'Sac Cuir Brun', 'Sac cuir fait main', 189.99, 149.99, 15, true, NOW(), 42),
                                                                                                                    (2, 'Sac Bandoulière', 'Coton brodé traditionnel', 79.90, NULL, 30, true, NOW(), 18),
                                                                                                                    (2, 'Pochette Dorée', 'Pour soirée', 59.00, 45.00, 8, true, NOW(), 27);

INSERT INTO product_categories (product_id, category_id) VALUES (1,1),(2,1),(3,1);
INSERT INTO product_images (product_id, image_url) VALUES
                                                       (1, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600'),
                                                       (2, 'https://images.unsplash.com/photo-1594938298603-c8148c4b4d4b?w=600'),
                                                       (3, 'https://images.unsplash.com/photo-1566150905458-1bf1fc113f0d?w=600');

INSERT INTO coupon (code, type, valeur, date_expiration, usages_max, usages_actuels, actif) VALUES
                                                                                                ('BIENVENUE10', 'PERCENT', 10.00, '2027-12-31', 100, 0, true),
                                                                                                ('REMISE20', 'FIXED', 20.00, '2026-12-31', 200, 0, true);