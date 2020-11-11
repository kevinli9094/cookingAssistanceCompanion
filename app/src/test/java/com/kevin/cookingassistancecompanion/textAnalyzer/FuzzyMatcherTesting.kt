package com.kevin.cookingassistancecompanion.textAnalyzer

import android.util.Log
import com.intuit.fuzzymatcher.component.MatchService
import com.intuit.fuzzymatcher.domain.Document
import com.intuit.fuzzymatcher.domain.Document.Builder
import com.intuit.fuzzymatcher.domain.Element
import com.intuit.fuzzymatcher.domain.ElementType
import com.intuit.fuzzymatcher.domain.Match
import com.intuit.fuzzymatcher.domain.MatchType
import org.junit.Test


class FuzzyMatcherTesting {

    val ingredientList = listOf<String>(
        "rua beef chuck flat",
        "rua pork side rib-cut",
        "ko-le cabbage",
        "garlic elephant",
        "chinese lettuce",
        "green onion",
        "snow pea leaf",
        "yu-choy",
        "green beans",
        "youjia bay leaf",
        "lao jue chu five spice peanuts",
        "sunity mung bean herbal jelly"
    )

    val outputResult: List<String> = listOf(
        "GARLIC ELEPHANT",
        "ORION SPICY BBQ FLU CORN CHIPS",
        "KO-LE CABBAGE",
        "RUA BEEF CHUCK FLAT",
        "RUA PORK SIDE RIB-CUT",
        "GREEN ONION",
        "YOUJIA BAY LEAF",
        "CHINESE LETIUCE",
        "SUNITY HUNG BEAN HERBAL JELLY",
        "LAO JUE CHU FIVE SPICE PEANUTS",
        "WU-MU DRY NOODLE-L",
        "LAO JUE CHU FIUE SPICE PEANUTS",
        "CHINESE LETTUCE",
        "P 2.295 kg O $1.52/kg",
        "( SO. 49ea.",
        "JERN FU PREPARED SHREDDED SQUI",
        "P 2.295 kg $1.52/kg",
        "SUNITY MUNG BEAN HERBAL JELLY",
        "WU-MU DRY NOODLE-",
        "JERN FU PREPARED SHREDDED SUI",
        "JEAN FU PREPARED SHRE DDED SQUI",
        "T&T Supernarket",
        "O132",
        "W.",
        "SAlus",
        "PEA CHACKER",
        "V12.19 &",
        "UU-HU DAY HOODLE-L",
        "AMlAGb-I1",
        "JON FU PREPMED SHMEDOED SUJI",
        "YouJIA BY1",
        "SUNIIY HUNG BEAN HERBAL JELLY",
        "SAL) RUA BEEF CHUCK FLA",
        ".334 9",
        "SALE) KO-LE CRBBAGt",
        ".32O Kg 9 S8.8O/K9",
        "CHINESE LETTULE",
        "a sb.53/k9",
        "e8.",
        ".8O5 19913.21/9",
        "(SALL YU CHOY",
        "Y iPROU",
        "RUR B HUCK FLA",
        "SS A9 22",
        "O2/k",
        "RUR PORK SIDE RIB-CUT",
        "HLE KU-LE CABBAGE",
        "BARLIC ELEPHANT",
        "DINESE LETTUCE",
        "u3261327",
        "BROCERY",
        "KOLOKO SAuGASE FLI PER CRRCAER",
        "-DRY MODLE-L",
        "EAl7E-E",
        "ERN FU PREPHRED SHREDUED SR",
        "OR TON SPEY 2O FL CERH CHPS",
        "YOLUTH BBV LEAF",
        "LAD JUE CHL FLUE SPIE PEL",
        "SUNITY SUNE SE ER E",
        "SLE) RUR EE CHICK FAT",
        "Ru&RK S",
        "PRDUCE",
        "AR",
        "ONINE L8T",
        "KOLON SRLUSREE RLU PER CRRCKR",
        "E F PRcPRED SHREDUED SUUL",
        "R DN SPOY B FLU CURH CHIPS",
        "OLTHS LEF",
        "HU FDE SP IIE PEANUTS",
        "SURETY HUNE BEAN ERL JELLY",
        "SHEHU EE CHCK FLAT",
        "KOLOKO SRUSAGE FLU PEA CRACKER",
        "WU-HU DRY NOODLE-L",
        "JERN FU PREPARED SHREDDED SUUI",
        "kFLAAT6O8)",
        "FPE SEEA:A*R",
        "SUNITY HUNG BEAN HERBAL JLLY",
        "WU-NU DRY NOODLE-L",
        "tRLK16O8)",
        "(2SO.49ea.",
        "U-MU DRY NOODLE-L",
        "JEAN FU PRE PARED SHRE DOED SVI",
        "LAO JUE CHU FIUE SPICE PEANUIS",
        "EHB iEt PEANUs",
        "P 2.295 kg 9 $1.52/k9",
        "C 4",
        "SO",
        "JEAN FU PRE PARED SHREDDED SQUI",
        "lkiHAAMI6OO)",
        "FRESEREK",
        "M$2.29",
        "RWA PORK SIDE RIB-CUT",
        "EREEN ONION",
        "( 2SO. 49ea.",
        "lkAA16O6)",
        "RTWE&PEB9RA:A*R",
        "TETUM",
        "AS)",
        "(2 SO. 49ea.",
        "WU-MU DRY NDODL E-L",
        "alIAK6OO)",
        "OUJIA BAY LEAF",
        "(2SO",
        "49ea.",
        "E ETIUM",
        "DHFSTE",
        "C (4D",
        "(2 e SO. 49ea.",
        "UU-MU DRY NOODLE-L",
        "KjAA16OO)",
        "YOUuJIA BAY LERF",
        "Bf A",
        "GREEN ONLON",
        "JEAN FU PREPARED SHREDDED SQUI",
        "jAAA16O6)",
        "FREZEREK",
        "HEAT",
        "AITHFSE",
        "hht>AjRLSt16O)",
        "YOUJIR BAY LEAF",
        "RUWA BEEF CHUCK FLAT",
        "(2 SO.49ea",
        "JERN FU PREPARED SHRE DOED SQUI",
        "( 2SO.49ea",
        "JEAN FU PREPARED SHREDDED SUUI",
        "HMDHFETR",
        "HRL ELEPHANT"
    )

    @Test
    fun `test`() {

        val documentList: List<Document> =
            outputResult.mapIndexed { index, contact ->
                Builder(index.toString())
                    .addElement(
                        Element.Builder<String>()
                            .setValue(contact)
                            .setType(ElementType.TEXT)
                            .setMatchType(MatchType.EQUALITY)
                            .setPreProcessingFunction {
                                return@setPreProcessingFunction it
                            }
                            .createElement()
                    )
                    .createDocument()
            }

        val existingDoc = ingredientList.mapIndexed { index, contact ->
            Builder(index.toString())
                .addElement(
                    Element.Builder<String>()
                        .setValue(contact)
                        .setType(ElementType.TEXT)
                        .setMatchType(MatchType.EQUALITY)
                        .setPreProcessingFunction {
                            return@setPreProcessingFunction it
                        }
                        .setThreshold(0.6)
                        .createElement()
                )
                .createDocument()
        }

        val matchService = MatchService()
        val result =
            matchService.applyMatch(existingDoc, documentList)

        val fakeString = "asdf"

//        val tag = "fake tag:"
//        result.forEach {(key, list) ->
//            Log.i(tag, "key: $key")
//            Log.i(tag, "matches: $list")
//        }
    }
}