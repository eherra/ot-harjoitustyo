# Testausdokumentti

Ohjelmaa on testattu automaattisia JUnit-yksikkötestejä apuna käyttäen sekä manuaalisesti graafisen liittymän kautta, sekä täten samalla peliloogiikka on ollut testauksen alle.

## Yksikkö- ja integraatiotestaus

### Pelilogiikka

Ohjelman moottorina toimivat GameLogic-, ScoreBoard- ja MoveExecutor-luokat (game2048.domain) ovat testattu laajasti sekä yksikkötestein että manuaalisesti, jotta ne noudattavat tarkoin pelin sääntöjä
(oikeat yhdistykset paloille, ei liika yhdistymiä, random-palasen lisäys, pistelaskun oikeallisuus).

### Tiedon (pisteet) pysyväistallennus
Pysyväistallennusta ja sen hakemiseen olevia metodeita on testattu laajasti, jotta haettu tieto on oikeata sekä tallennus tapahtuu oikeaan paikkaan sekä oikeille tiedoilla. 

Testien alussa luodaan @Before-annotiaatiolla merkatulla metodilla **test.db**-tiedosto, johon tallennetaan testien aikana olevat tietokantatiedot. Kyseinen tiedosto poistetaan testien päätyttyä @After-annotiaatiolla merkatulla metodilla. 

### Testauskattavuus

Sovelluksen testattujen osien testauksen rivikattavuus on 86% ja haarautumiskattavuus 88%.
<img src= "https://github.com/eherra/ot-harjoitustyo/blob/main/dokumentaatio/kuvat/coverageee.png">

Pakkauksen **utils** Square-luokan _getSquareStack_-metodia ei ole testattu, sillä tämä osa UI:n toiminallisuutta.
<img src= "https://github.com/eherra/ot-harjoitustyo/blob/main/dokumentaatio/kuvat/utilss.png">



### Asennus ja konfigurointi
Sovellusta on testattu OSX- että Linux-pohjaisissa ympäristöissä ja sovellus on toiminut moitteettomasti.

### Toiminnallisuudet
Kaikki vaatimusmäärittelydokumentissa mainitut toiminnallisuudet ovat toteutettu.

