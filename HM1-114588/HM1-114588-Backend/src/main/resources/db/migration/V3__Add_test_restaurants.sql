-- Add Aveiro location
INSERT INTO locations (name, latitude, longitude, created_at)
VALUES ('Aveiro', 40.6405, -8.6538, CURRENT_TIMESTAMP);

-- Add restaurants
INSERT INTO restaurants (name, description, capacity, operating_hours, contact_info, location_id, created_at)
VALUES 
    ('Cantina Crasto', 'Uma cantina universitária no campus de Crasto com comida variada para estudantes e professores.', 200, 'Segunda-Sexta: 12:00-15:00, 19:00-21:00', 'cantina.crasto@ua.pt', 
     (SELECT id FROM locations WHERE name = 'Aveiro'), CURRENT_TIMESTAMP),
    ('Cantina Santiago', 'Cantina universitária localizada no campus de Santiago, oferecendo diversas opções de refeições.', 250, 'Segunda-Sexta: 12:00-15:00, 19:00-21:00', 'cantina.santiago@ua.pt', 
     (SELECT id FROM locations WHERE name = 'Aveiro'), CURRENT_TIMESTAMP),
    ('Campi Grelhados', 'Restaurante especializado em grelhados variados, com ambiente acolhedor e preços acessíveis.', 80, 'Segunda-Domingo: 12:00-15:00, 19:00-23:00', 'campi.grelhados@email.com', 
     (SELECT id FROM locations WHERE name = 'Aveiro'), CURRENT_TIMESTAMP),
    ('Campi TrêsDê', 'Restaurante moderno com ambiente inovador e menu criativo. Especializado em gastronomia portuguesa contemporânea.', 60, 'Terça-Domingo: 12:00-15:00, 19:00-22:30', 'campi.tresde@email.com', 
     (SELECT id FROM locations WHERE name = 'Aveiro'), CURRENT_TIMESTAMP);

-- Add schedules for each restaurant (using explicit dates)
INSERT INTO schedules (start_date, end_date, restaurant_id)
VALUES 
    ('2024-04-10', '2024-07-09', (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')),
    ('2024-04-10', '2024-07-09', (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')),
    ('2024-04-10', '2024-07-09', (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')),
    ('2024-04-10', '2024-07-09', (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'));

-- Add meals for Cantina Crasto
INSERT INTO meals (name, meal_type, start_time, end_time, description, price, restaurant_id, schedule_id)
VALUES 
    ('Almoço', 'LUNCH', '12:00', '15:00', 'Almoço diário com opções variadas', '2.70', 
        (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))),
    ('Jantar', 'DINNER', '19:00', '21:00', 'Jantar com diversas opções', '2.70', 
        (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')));

-- Add meals for Cantina Santiago
INSERT INTO meals (name, meal_type, start_time, end_time, description, price, restaurant_id, schedule_id)
VALUES 
    ('Almoço', 'LUNCH', '12:00', '15:00', 'Almoço diário com opções variadas', '2.70', 
        (SELECT id FROM restaurants WHERE name = 'Cantina Santiago'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago'))),
    ('Jantar', 'DINNER', '19:00', '21:00', 'Jantar com diversas opções', '2.70', 
        (SELECT id FROM restaurants WHERE name = 'Cantina Santiago'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')));

-- Add meals for Campi Grelhados
INSERT INTO meals (name, meal_type, start_time, end_time, description, price, restaurant_id, schedule_id)
VALUES 
    ('Almoço', 'LUNCH', '12:00', '15:00', 'Especialidades grelhadas para o almoço', '8.50', 
        (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'))),
    ('Jantar', 'DINNER', '19:00', '23:00', 'Grelhados especiais para o jantar', '10.50', 
        (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')));

-- Add meals for Campi TrêsDê
INSERT INTO meals (name, meal_type, start_time, end_time, description, price, restaurant_id, schedule_id)
VALUES 
    ('Almoço Gourmet', 'LUNCH', '12:00', '15:00', 'Experiência gastronômica para o almoço', '12.50', 
        (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))),
    ('Jantar Premium', 'DINNER', '19:00', '22:30', 'Jantar gourmet com opções especiais', '15.50', 
        (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'),
        (SELECT id FROM schedules WHERE restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê')));

-- Add dishes
INSERT INTO dishes (name, description, type, is_available, price)
VALUES 
    ('Caldo Verde', 'Sopa tradicional portuguesa com couve e chouriço', 'Sopa', TRUE, 1.50),
    ('Sopa de Legumes', 'Sopa de legumes frescos da época', 'Sopa', TRUE, 1.50),
    ('Carne de Porco à Alentejana', 'Carne de porco com amêijoas e batata frita aos cubos', 'Carne', TRUE, 4.50),
    ('Bifes de Frango Grelhados', 'Bifes de frango grelhados com ervas aromáticas', 'Carne', TRUE, 4.00),
    ('Bacalhau com Natas', 'Bacalhau desfiado com natas e batata palha', 'Peixe', TRUE, 5.00),
    ('Pescada Cozida', 'Filetes de pescada cozidos com batata e legumes', 'Peixe', TRUE, 4.50),
    ('Lasanha Vegetariana', 'Lasanha de legumes com molho de tomate e queijo', 'Vegetariano', TRUE, 4.00),
    ('Hambúrguer de Novilho', 'Hambúrguer de novilho com queijo, bacon e molho especial', 'Hamburger', TRUE, 6.50),
    ('Pizza Margherita', 'Pizza tradicional com molho de tomate, mozzarella e manjericão', 'Pizza', TRUE, 7.00),
    ('Salada de Atum', 'Salada verde com atum, milho, tomate e cebola', 'Salada', TRUE, 5.00),
    ('Mousse de Chocolate', 'Mousse de chocolate caseira', 'Sobremesa', TRUE, 2.00),
    ('Salada de Frutas', 'Salada de frutas frescas da época', 'Sobremesa', TRUE, 1.50),
    ('Água Mineral', 'Garrafa de água mineral 0.5L', 'Bebida', TRUE, 1.00),
    ('Refrigerante', 'Refrigerante à escolha 0.33L', 'Bebida', TRUE, 1.50),
    ('Picanha Grelhada', 'Picanha grelhada com batatas rústicas e legumes', 'Carne', TRUE, 12.50),
    ('Costeletão na Brasa', 'Costeletão grelhado na brasa com molho especial', 'Carne', TRUE, 14.00),
    ('Peixe do Dia Grelhado', 'Peixe fresco do dia grelhado com legumes', 'Peixe', TRUE, 11.00),
    ('Risotto de Cogumelos', 'Risotto cremoso com variedade de cogumelos', 'Vegetariano', TRUE, 9.50),
    ('Polvo à Lagareiro', 'Polvo grelhado com azeite, alho e batata a murro', 'Peixe', TRUE, 16.00),
    ('Espetada de Novilho', 'Espetada de novilho com legumes grelhados', 'Carne', TRUE, 13.50);

-- Add menus for Cantina Crasto Almoço (using explicit dates)
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Almoço Segunda', 'Menu do almoço para segunda-feira', '2024-04-11', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE),
    ('Menu Almoço Terça', 'Menu do almoço para terça-feira', '2024-04-12', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE),
    ('Menu Almoço Quarta', 'Menu do almoço para quarta-feira', '2024-04-13', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE);

-- Add menus for Cantina Crasto Jantar
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Jantar Segunda', 'Menu do jantar para segunda-feira', '2024-04-11', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE),
    ('Menu Jantar Terça', 'Menu do jantar para terça-feira', '2024-04-12', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE),
    ('Menu Jantar Quarta', 'Menu do jantar para quarta-feira', '2024-04-13', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto')), TRUE);

-- Add menus for Cantina Santiago Almoço
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Almoço Segunda', 'Menu do almoço para segunda-feira', '2024-04-11', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE),
    ('Menu Almoço Terça', 'Menu do almoço para terça-feira', '2024-04-12', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE),
    ('Menu Almoço Quarta', 'Menu do almoço para quarta-feira', '2024-04-13', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE);

-- Add menus for Cantina Santiago Jantar
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Jantar Segunda', 'Menu do jantar para segunda-feira', '2024-04-11', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE),
    ('Menu Jantar Terça', 'Menu do jantar para terça-feira', '2024-04-12', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE),
    ('Menu Jantar Quarta', 'Menu do jantar para quarta-feira', '2024-04-13', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Santiago')), TRUE);

-- Add menus for Campi Grelhados Almoço
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Executivo Almoço', 'Menu executivo para o almoço', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')), TRUE),
    ('Menu Especial Almoço', 'Menu especial para o almoço', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')), TRUE);

-- Add menus for Campi Grelhados Jantar
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Executivo Jantar', 'Menu executivo para o jantar', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')), TRUE),
    ('Menu Especial Jantar', 'Menu especial para o jantar', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Jantar' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados')), TRUE);

-- Add menus for Campi TrêsDê Almoço
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Degustação Almoço', 'Menu de degustação para o almoço', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Almoço Gourmet' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê')), TRUE),
    ('Menu Chef Almoço', 'Menu do chef para o almoço', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Almoço Gourmet' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê')), TRUE);

-- Add menus for Campi TrêsDê Jantar
INSERT INTO menus (name, description, date, meal_id, is_available)
VALUES 
    ('Menu Degustação Jantar', 'Menu de degustação para o jantar', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê')), TRUE),
    ('Menu Chef Jantar', 'Menu do chef para o jantar', '2024-04-10', 
        (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê')), TRUE);

-- Associate dishes with menus (many-to-many relationship)
-- Cantina Crasto Almoço Segunda
INSERT INTO menu_dishes (menu_id, dish_id)
VALUES 
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Caldo Verde')),
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Carne de Porco à Alentejana')),
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Bacalhau com Natas')),
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Lasanha Vegetariana')),
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Mousse de Chocolate')),
    ((SELECT id FROM menus WHERE name = 'Menu Almoço Segunda' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Cantina Crasto'))), 
     (SELECT id FROM dishes WHERE name = 'Salada de Frutas'));

-- Special menus for Campi Grelhados
INSERT INTO menu_dishes (menu_id, dish_id)
VALUES 
    ((SELECT id FROM menus WHERE name = 'Menu Executivo Almoço' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'))), 
     (SELECT id FROM dishes WHERE name = 'Sopa de Legumes')),
    ((SELECT id FROM menus WHERE name = 'Menu Executivo Almoço' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'))), 
     (SELECT id FROM dishes WHERE name = 'Picanha Grelhada')),
    ((SELECT id FROM menus WHERE name = 'Menu Executivo Almoço' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'))), 
     (SELECT id FROM dishes WHERE name = 'Salada de Frutas')),
    ((SELECT id FROM menus WHERE name = 'Menu Executivo Almoço' AND meal_id = (SELECT id FROM meals WHERE name = 'Almoço' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi Grelhados'))), 
     (SELECT id FROM dishes WHERE name = 'Água Mineral'));

-- Special menus for Campi TrêsDê
INSERT INTO menu_dishes (menu_id, dish_id)
VALUES 
    ((SELECT id FROM menus WHERE name = 'Menu Degustação Jantar' AND meal_id = (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))), 
     (SELECT id FROM dishes WHERE name = 'Sopa de Legumes')),
    ((SELECT id FROM menus WHERE name = 'Menu Degustação Jantar' AND meal_id = (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))), 
     (SELECT id FROM dishes WHERE name = 'Polvo à Lagareiro')),
    ((SELECT id FROM menus WHERE name = 'Menu Degustação Jantar' AND meal_id = (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))), 
     (SELECT id FROM dishes WHERE name = 'Risotto de Cogumelos')),
    ((SELECT id FROM menus WHERE name = 'Menu Degustação Jantar' AND meal_id = (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))), 
     (SELECT id FROM dishes WHERE name = 'Mousse de Chocolate')),
    ((SELECT id FROM menus WHERE name = 'Menu Degustação Jantar' AND meal_id = (SELECT id FROM meals WHERE name = 'Jantar Premium' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Campi TrêsDê'))), 
     (SELECT id FROM dishes WHERE name = 'Água Mineral')); 