% generate problem of size 500
reachable(X,Y) :- edge(X,Y).
reachable(X,Y) :- edge(X,Z), reachable(Z,Y).
increasing(X,Y) :- edge(X,Y), X<Y.
increasing(X,Y) :- edge(X,Z), X<Z, increasing(Z,Y).
edge(0, 1).
edge(1, 2).
edge(2, 3).
edge(3, 4).
edge(4, 5).
edge(5, 6).
edge(6, 7).
edge(7, 8).
edge(8, 9).
edge(9, 10).
edge(10, 11).
edge(11, 12).
edge(12, 13).
edge(13, 14).
edge(14, 15).
edge(15, 16).
edge(16, 17).
edge(17, 18).
edge(18, 19).
edge(19, 20).
edge(20, 21).
edge(21, 22).
edge(22, 23).
edge(23, 24).
edge(24, 25).
edge(25, 26).
edge(26, 27).
edge(27, 28).
edge(28, 29).
edge(29, 30).
edge(30, 31).
edge(31, 32).
edge(32, 33).
edge(33, 34).
edge(34, 35).
edge(35, 36).
edge(36, 37).
edge(37, 38).
edge(38, 39).
edge(39, 40).
edge(40, 41).
edge(41, 42).
edge(42, 43).
edge(43, 44).
edge(44, 45).
edge(45, 46).
edge(46, 47).
edge(47, 48).
edge(48, 49).
edge(49, 50).
edge(50, 51).
edge(51, 52).
edge(52, 53).
edge(53, 54).
edge(54, 55).
edge(55, 56).
edge(56, 57).
edge(57, 58).
edge(58, 59).
edge(59, 60).
edge(60, 61).
edge(61, 62).
edge(62, 63).
edge(63, 64).
edge(64, 65).
edge(65, 66).
edge(66, 67).
edge(67, 68).
edge(68, 69).
edge(69, 70).
edge(70, 71).
edge(71, 72).
edge(72, 73).
edge(73, 74).
edge(74, 75).
edge(75, 76).
edge(76, 77).
edge(77, 78).
edge(78, 79).
edge(79, 80).
edge(80, 81).
edge(81, 82).
edge(82, 83).
edge(83, 84).
edge(84, 85).
edge(85, 86).
edge(86, 87).
edge(87, 88).
edge(88, 89).
edge(89, 90).
edge(90, 91).
edge(91, 92).
edge(92, 93).
edge(93, 94).
edge(94, 95).
edge(95, 96).
edge(96, 97).
edge(97, 98).
edge(98, 99).
edge(99, 100).
edge(100, 101).
edge(101, 102).
edge(102, 103).
edge(103, 104).
edge(104, 105).
edge(105, 106).
edge(106, 107).
edge(107, 108).
edge(108, 109).
edge(109, 110).
edge(110, 111).
edge(111, 112).
edge(112, 113).
edge(113, 114).
edge(114, 115).
edge(115, 116).
edge(116, 117).
edge(117, 118).
edge(118, 119).
edge(119, 120).
edge(120, 121).
edge(121, 122).
edge(122, 123).
edge(123, 124).
edge(124, 125).
edge(125, 126).
edge(126, 127).
edge(127, 128).
edge(128, 129).
edge(129, 130).
edge(130, 131).
edge(131, 132).
edge(132, 133).
edge(133, 134).
edge(134, 135).
edge(135, 136).
edge(136, 137).
edge(137, 138).
edge(138, 139).
edge(139, 140).
edge(140, 141).
edge(141, 142).
edge(142, 143).
edge(143, 144).
edge(144, 145).
edge(145, 146).
edge(146, 147).
edge(147, 148).
edge(148, 149).
edge(149, 150).
edge(150, 151).
edge(151, 152).
edge(152, 153).
edge(153, 154).
edge(154, 155).
edge(155, 156).
edge(156, 157).
edge(157, 158).
edge(158, 159).
edge(159, 160).
edge(160, 161).
edge(161, 162).
edge(162, 163).
edge(163, 164).
edge(164, 165).
edge(165, 166).
edge(166, 167).
edge(167, 168).
edge(168, 169).
edge(169, 170).
edge(170, 171).
edge(171, 172).
edge(172, 173).
edge(173, 174).
edge(174, 175).
edge(175, 176).
edge(176, 177).
edge(177, 178).
edge(178, 179).
edge(179, 180).
edge(180, 181).
edge(181, 182).
edge(182, 183).
edge(183, 184).
edge(184, 185).
edge(185, 186).
edge(186, 187).
edge(187, 188).
edge(188, 189).
edge(189, 190).
edge(190, 191).
edge(191, 192).
edge(192, 193).
edge(193, 194).
edge(194, 195).
edge(195, 196).
edge(196, 197).
edge(197, 198).
edge(198, 199).
edge(199, 200).
edge(200, 201).
edge(201, 202).
edge(202, 203).
edge(203, 204).
edge(204, 205).
edge(205, 206).
edge(206, 207).
edge(207, 208).
edge(208, 209).
edge(209, 210).
edge(210, 211).
edge(211, 212).
edge(212, 213).
edge(213, 214).
edge(214, 215).
edge(215, 216).
edge(216, 217).
edge(217, 218).
edge(218, 219).
edge(219, 220).
edge(220, 221).
edge(221, 222).
edge(222, 223).
edge(223, 224).
edge(224, 225).
edge(225, 226).
edge(226, 227).
edge(227, 228).
edge(228, 229).
edge(229, 230).
edge(230, 231).
edge(231, 232).
edge(232, 233).
edge(233, 234).
edge(234, 235).
edge(235, 236).
edge(236, 237).
edge(237, 238).
edge(238, 239).
edge(239, 240).
edge(240, 241).
edge(241, 242).
edge(242, 243).
edge(243, 244).
edge(244, 245).
edge(245, 246).
edge(246, 247).
edge(247, 248).
edge(248, 249).
edge(249, 250).
edge(250, 251).
edge(251, 252).
edge(252, 253).
edge(253, 254).
edge(254, 255).
edge(255, 256).
edge(256, 257).
edge(257, 258).
edge(258, 259).
edge(259, 260).
edge(260, 261).
edge(261, 262).
edge(262, 263).
edge(263, 264).
edge(264, 265).
edge(265, 266).
edge(266, 267).
edge(267, 268).
edge(268, 269).
edge(269, 270).
edge(270, 271).
edge(271, 272).
edge(272, 273).
edge(273, 274).
edge(274, 275).
edge(275, 276).
edge(276, 277).
edge(277, 278).
edge(278, 279).
edge(279, 280).
edge(280, 281).
edge(281, 282).
edge(282, 283).
edge(283, 284).
edge(284, 285).
edge(285, 286).
edge(286, 287).
edge(287, 288).
edge(288, 289).
edge(289, 290).
edge(290, 291).
edge(291, 292).
edge(292, 293).
edge(293, 294).
edge(294, 295).
edge(295, 296).
edge(296, 297).
edge(297, 298).
edge(298, 299).
edge(299, 300).
edge(300, 301).
edge(301, 302).
edge(302, 303).
edge(303, 304).
edge(304, 305).
edge(305, 306).
edge(306, 307).
edge(307, 308).
edge(308, 309).
edge(309, 310).
edge(310, 311).
edge(311, 312).
edge(312, 313).
edge(313, 314).
edge(314, 315).
edge(315, 316).
edge(316, 317).
edge(317, 318).
edge(318, 319).
edge(319, 320).
edge(320, 321).
edge(321, 322).
edge(322, 323).
edge(323, 324).
edge(324, 325).
edge(325, 326).
edge(326, 327).
edge(327, 328).
edge(328, 329).
edge(329, 330).
edge(330, 331).
edge(331, 332).
edge(332, 333).
edge(333, 334).
edge(334, 335).
edge(335, 336).
edge(336, 337).
edge(337, 338).
edge(338, 339).
edge(339, 340).
edge(340, 341).
edge(341, 342).
edge(342, 343).
edge(343, 344).
edge(344, 345).
edge(345, 346).
edge(346, 347).
edge(347, 348).
edge(348, 349).
edge(349, 350).
edge(350, 351).
edge(351, 352).
edge(352, 353).
edge(353, 354).
edge(354, 355).
edge(355, 356).
edge(356, 357).
edge(357, 358).
edge(358, 359).
edge(359, 360).
edge(360, 361).
edge(361, 362).
edge(362, 363).
edge(363, 364).
edge(364, 365).
edge(365, 366).
edge(366, 367).
edge(367, 368).
edge(368, 369).
edge(369, 370).
edge(370, 371).
edge(371, 372).
edge(372, 373).
edge(373, 374).
edge(374, 375).
edge(375, 376).
edge(376, 377).
edge(377, 378).
edge(378, 379).
edge(379, 380).
edge(380, 381).
edge(381, 382).
edge(382, 383).
edge(383, 384).
edge(384, 385).
edge(385, 386).
edge(386, 387).
edge(387, 388).
edge(388, 389).
edge(389, 390).
edge(390, 391).
edge(391, 392).
edge(392, 393).
edge(393, 394).
edge(394, 395).
edge(395, 396).
edge(396, 397).
edge(397, 398).
edge(398, 399).
edge(399, 400).
edge(400, 401).
edge(401, 402).
edge(402, 403).
edge(403, 404).
edge(404, 405).
edge(405, 406).
edge(406, 407).
edge(407, 408).
edge(408, 409).
edge(409, 410).
edge(410, 411).
edge(411, 412).
edge(412, 413).
edge(413, 414).
edge(414, 415).
edge(415, 416).
edge(416, 417).
edge(417, 418).
edge(418, 419).
edge(419, 420).
edge(420, 421).
edge(421, 422).
edge(422, 423).
edge(423, 424).
edge(424, 425).
edge(425, 426).
edge(426, 427).
edge(427, 428).
edge(428, 429).
edge(429, 430).
edge(430, 431).
edge(431, 432).
edge(432, 433).
edge(433, 434).
edge(434, 435).
edge(435, 436).
edge(436, 437).
edge(437, 438).
edge(438, 439).
edge(439, 440).
edge(440, 441).
edge(441, 442).
edge(442, 443).
edge(443, 444).
edge(444, 445).
edge(445, 446).
edge(446, 447).
edge(447, 448).
edge(448, 449).
edge(449, 450).
edge(450, 451).
edge(451, 452).
edge(452, 453).
edge(453, 454).
edge(454, 455).
edge(455, 456).
edge(456, 457).
edge(457, 458).
edge(458, 459).
edge(459, 460).
edge(460, 461).
edge(461, 462).
edge(462, 463).
edge(463, 464).
edge(464, 465).
edge(465, 466).
edge(466, 467).
edge(467, 468).
edge(468, 469).
edge(469, 470).
edge(470, 471).
edge(471, 472).
edge(472, 473).
edge(473, 474).
edge(474, 475).
edge(475, 476).
edge(476, 477).
edge(477, 478).
edge(478, 479).
edge(479, 480).
edge(480, 481).
edge(481, 482).
edge(482, 483).
edge(483, 484).
edge(484, 485).
edge(485, 486).
edge(486, 487).
edge(487, 488).
edge(488, 489).
edge(489, 490).
edge(490, 491).
edge(491, 492).
edge(492, 493).
edge(493, 494).
edge(494, 495).
edge(495, 496).
edge(496, 497).
edge(497, 498).
edge(498, 499).
edge(499, 500).
edge(500, 0).
