-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Client :  localhost:3306
-- Généré le :  Sam 08 Avril 2017 à 19:34
-- Version du serveur :  5.6.35
-- Version de PHP :  5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données :  `how_to`
--

-- --------------------------------------------------------

--
-- Structure de la table `category_howto`
--

CREATE TABLE `category_howto` (
  `id` int(11) NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `position` int(11) NOT NULL,
  `description` longtext COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `comment_howto`
--

CREATE TABLE `comment_howto` (
  `id` int(11) NOT NULL,
  `guide_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL,
  `enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `device_howto`
--

CREATE TABLE `device_howto` (
  `id` int(11) NOT NULL,
  `token` longtext COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fos_user_howto`
--

CREATE TABLE `fos_user_howto` (
  `id` int(11) NOT NULL,
  `media_id` int(11) DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `username_canonical` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email_canonical` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `salt` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `locked` tinyint(1) NOT NULL,
  `expired` tinyint(1) NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  `confirmation_token` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password_requested_at` datetime DEFAULT NULL,
  `roles` longtext COLLATE utf8_unicode_ci NOT NULL COMMENT '(DC2Type:array)',
  `credentials_expired` tinyint(1) NOT NULL,
  `credentials_expire_at` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Contenu de la table `fos_user_howto`
--


-- --------------------------------------------------------

--
-- Structure de la table `gallery_howto`
--

CREATE TABLE `gallery_howto` (
  `id` int(11) NOT NULL,
  `titre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `guide_how_to`
--

CREATE TABLE `guide_how_to` (
  `id` int(11) NOT NULL,
  `media_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `comment` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `medias_gallerys_howto`
--

CREATE TABLE `medias_gallerys_howto` (
  `gallery_id` int(11) NOT NULL,
  `media_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `media_howto`
--

CREATE TABLE `media_howto` (
  `id` int(11) NOT NULL,
  `titre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  `enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `step_howto`
--

CREATE TABLE `step_howto` (
  `id` int(11) NOT NULL,
  `media_id` int(11) DEFAULT NULL,
  `guide_id` int(11) NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  `position` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `support_contact_howto`
--

CREATE TABLE `support_contact_howto` (
  `id` int(11) NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `subject` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `message` longtext COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `category_howto`
--
ALTER TABLE `category_howto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UNIQ_23F6DD632B36786B` (`title`);

--
-- Index pour la table `comment_howto`
--
ALTER TABLE `comment_howto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_625F0ADBD7ED1D4B` (`guide_id`),
  ADD KEY `IDX_625F0ADBA76ED395` (`user_id`);

--
-- Index pour la table `device_howto`
--
ALTER TABLE `device_howto`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `fos_user_howto`
--
ALTER TABLE `fos_user_howto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UNIQ_FE38C9D192FC23A8` (`username_canonical`),
  ADD UNIQUE KEY `UNIQ_FE38C9D1A0D96FBF` (`email_canonical`),
  ADD KEY `IDX_FE38C9D1EA9FDD75` (`media_id`);

--
-- Index pour la table `gallery_howto`
--
ALTER TABLE `gallery_howto`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `guide_how_to`
--
ALTER TABLE `guide_how_to`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_311229F2EA9FDD75` (`media_id`),
  ADD KEY `IDX_311229F212469DE2` (`category_id`);

--
-- Index pour la table `medias_gallerys_howto`
--
ALTER TABLE `medias_gallerys_howto`
  ADD PRIMARY KEY (`gallery_id`,`media_id`),
  ADD KEY `IDX_F17A40A24E7AF8F` (`gallery_id`),
  ADD KEY `IDX_F17A40A2EA9FDD75` (`media_id`);

--
-- Index pour la table `media_howto`
--
ALTER TABLE `media_howto`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `step_howto`
--
ALTER TABLE `step_howto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_D7E765D5EA9FDD75` (`media_id`),
  ADD KEY `IDX_D7E765D5D7ED1D4B` (`guide_id`);

--
-- Index pour la table `support_contact_howto`
--
ALTER TABLE `support_contact_howto`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `category_howto`
--
ALTER TABLE `category_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT pour la table `comment_howto`
--
ALTER TABLE `comment_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;
--
-- AUTO_INCREMENT pour la table `device_howto`
--
ALTER TABLE `device_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT pour la table `fos_user_howto`
--
ALTER TABLE `fos_user_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT pour la table `gallery_howto`
--
ALTER TABLE `gallery_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT pour la table `guide_how_to`
--
ALTER TABLE `guide_how_to`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT pour la table `media_howto`
--
ALTER TABLE `media_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;
--
-- AUTO_INCREMENT pour la table `step_howto`
--
ALTER TABLE `step_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;
--
-- AUTO_INCREMENT pour la table `support_contact_howto`
--
ALTER TABLE `support_contact_howto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `comment_howto`
--
ALTER TABLE `comment_howto`
  ADD CONSTRAINT `FK_625F0ADBA76ED395` FOREIGN KEY (`user_id`) REFERENCES `fos_user_howto` (`id`),
  ADD CONSTRAINT `FK_625F0ADBD7ED1D4B` FOREIGN KEY (`guide_id`) REFERENCES `guide_how_to` (`id`);

--
-- Contraintes pour la table `fos_user_howto`
--
ALTER TABLE `fos_user_howto`
  ADD CONSTRAINT `FK_FE38C9D1EA9FDD75` FOREIGN KEY (`media_id`) REFERENCES `media_howto` (`id`);

--
-- Contraintes pour la table `guide_how_to`
--
ALTER TABLE `guide_how_to`
  ADD CONSTRAINT `FK_311229F212469DE2` FOREIGN KEY (`category_id`) REFERENCES `category_howto` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `FK_311229F2EA9FDD75` FOREIGN KEY (`media_id`) REFERENCES `media_howto` (`id`);

--
-- Contraintes pour la table `medias_gallerys_howto`
--
ALTER TABLE `medias_gallerys_howto`
  ADD CONSTRAINT `FK_F17A40A24E7AF8F` FOREIGN KEY (`gallery_id`) REFERENCES `gallery_howto` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_F17A40A2EA9FDD75` FOREIGN KEY (`media_id`) REFERENCES `media_howto` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `step_howto`
--
ALTER TABLE `step_howto`
  ADD CONSTRAINT `FK_D7E765D5D7ED1D4B` FOREIGN KEY (`guide_id`) REFERENCES `guide_how_to` (`id`),
  ADD CONSTRAINT `FK_D7E765D5EA9FDD75` FOREIGN KEY (`media_id`) REFERENCES `media_howto` (`id`);



INSERT INTO `fos_user_howto` (`id`, `media_id`, `username`, `username_canonical`, `email`, `email_canonical`, `enabled`, `salt`, `password`, `last_login`, `locked`, `expired`, `expires_at`, `confirmation_token`, `password_requested_at`, `roles`, `credentials_expired`, `credentials_expire_at`, `name`, `type`) VALUES
(1, NULL, 'ADMIN', 'admin', 'ADMIN', 'admin', 1, 'djtfgbufxr4gwk4k0gss4sgs4k48wc4', '$2y$13$djtfgbufxr4gwk4k0gss4e/GWxLE60yfqNel24unSLeYzhjG19VqS', '2017-04-07 22:19:35', 0, 0, NULL, NULL, NULL, 'a:1:{i:0;s:10:\"ROLE_ADMIN\";}', 0, NULL, 'samir toni', 'email')
