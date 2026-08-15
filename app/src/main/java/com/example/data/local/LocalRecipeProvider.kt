package com.example.data.local

import com.example.data.model.*

object LocalRecipeProvider {
    val recipes: List<Recipe> = listOf(
        // === BREAKFAST (Frühstück) ===
        Recipe(
            id = "b1",
            name = "Deutsches Frühstück",
            emoji = "🍳",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.BREAKFAST,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 10,
            skillLevel = 1,
            costPerServingEUR = 1.20,
            proteinGrams = 14,
            kcal = 350,
            ingredients = listOf(
                Ingredient("Vollkornbrot", 2.0, "Scheibe", Aisle.PANTRY),
                Ingredient("Gouda", 50.0, "g", Aisle.DAIRY),
                Ingredient("Butter", 15.0, "g", Aisle.DAIRY),
                Ingredient("Gurke", 0.25, "Stück", Aisle.PRODUCE),
                Ingredient("Ei", 1.0, "Stück", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Das Ei ca. 7 Minuten kochen, abschrecken und pellen.",
                "Die Vollkornbrotscheiben mit Butter bestreichen.",
                "Mit Gouda-Käse belegen und mit Gurkenscheiben garnieren.",
                "Das hartgekochte Ei salzen und dazu servieren."
            )
        ),
        Recipe(
            id = "b2",
            name = "Rührei mit Kräutern",
            emoji = "🥚",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.BREAKFAST,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 10,
            skillLevel = 1,
            costPerServingEUR = 1.00,
            proteinGrams = 16,
            kcal = 280,
            ingredients = listOf(
                Ingredient("Ei", 3.0, "Stück", Aisle.DAIRY),
                Ingredient("Schnittlauch", 0.25, "Bund", Aisle.PRODUCE),
                Ingredient("Butter", 10.0, "g", Aisle.DAIRY),
                Ingredient("Milch", 30.0, "ml", Aisle.DAIRY),
                Ingredient("Vollkornbrot", 1.0, "Scheibe", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Die Eier mit der Milch in einer Schüssel verquirlen und mit Salz und Pfeffer würzen.",
                "Den Schnittlauch in feine Röllchen schneiden.",
                "Butter in einer Pfanne schmelzen lassen.",
                "Die Eimischung hineingeben und bei mittlerer Hitze stocken lassen, dabei sanft rühren.",
                "Kurz vor Schluss den Schnittlauch unterheben und auf einer Scheibe Vollkornbrot anrichten."
            )
        ),
        Recipe(
            id = "b3",
            name = "Apfel-Zimt Porridge",
            emoji = "🥣",
            cuisine = Cuisine.MIX,
            mealType = MealType.BREAKFAST,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 15,
            skillLevel = 1,
            costPerServingEUR = 0.80,
            proteinGrams = 10,
            kcal = 310,
            ingredients = listOf(
                Ingredient("Haferflocken", 50.0, "g", Aisle.PANTRY),
                Ingredient("Milch", 200.0, "ml", Aisle.DAIRY),
                Ingredient("Apfel", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Zimt", 0.5, "TL", Aisle.PANTRY),
                Ingredient("Honig", 1.0, "TL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Haferflocken mit Milch in einem kleinen Topf zum Kochen bringen.",
                "Bei schwacher Hitze ca. 5 Minuten köcheln lassen, bis ein cremiger Brei entsteht.",
                "Den Apfel raspeln oder in kleine Würfel schneiden.",
                "Apfelstücke, Zimt und Honig unter das warme Porridge rühren und warm genießen."
            )
        ),
        Recipe(
            id = "b4",
            name = "Avocado Toast",
            emoji = "🥑",
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.BREAKFAST,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 10,
            skillLevel = 1,
            costPerServingEUR = 1.80,
            proteinGrams = 8,
            kcal = 320,
            ingredients = listOf(
                Ingredient("Avocado", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Vollkornbrot", 2.0, "Scheibe", Aisle.PANTRY),
                Ingredient("Tomate", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Olivenöl", 1.0, "TL", Aisle.PANTRY),
                Ingredient("Zitrone", 0.25, "Stück", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Das Brot im Toaster oder in einer Pfanne knusprig anrösten.",
                "Die Avocadohälfte schälen, mit einer Gabel zerdrücken, mit Zitronensaft, Salz und Pfeffer abschmecken.",
                "Die Tomate in feine Scheiben schneiden.",
                "Das Avocado-Püree auf dem warmen Brot verteilen.",
                "Mit Tomatenscheiben belegen und mit etwas Olivenöl beträufeln."
            )
        ),
        Recipe(
            id = "b5",
            name = "Frische Laugenstange mit Belag",
            emoji = "🥨",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.BREAKFAST,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 5,
            skillLevel = 1,
            costPerServingEUR = 1.50,
            proteinGrams = 12,
            kcal = 340,
            ingredients = listOf(
                Ingredient("Laugenstange", 1.0, "Stück", Aisle.PANTRY),
                Ingredient("Frischkäse", 30.0, "g", Aisle.DAIRY),
                Ingredient("Gurke", 0.25, "Stück", Aisle.PRODUCE),
                Ingredient("Kresse", 1.0, "Karton", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Die Laugenstange der Länge nach aufschneiden.",
                "Beide Hälften großzügig mit Frischkäse bestreichen.",
                "Gurke in dünne Scheiben schneiden und auf dem Frischkäse verteilen.",
                "Mit frischer Kresse bestreuen und zuklappen."
            )
        ),

        // === LUNCH (Mittagessen) ===
        Recipe(
            id = "l1",
            name = "Roter Linseneintopf",
            emoji = "🍲",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.LUNCH,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 30,
            skillLevel = 1,
            costPerServingEUR = 1.10,
            proteinGrams = 18,
            kcal = 380,
            ingredients = listOf(
                Ingredient("Rote Linsen", 80.0, "g", Aisle.PANTRY),
                Ingredient("Möhre", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Kartoffeln", 2.0, "Stück", Aisle.PRODUCE),
                Ingredient("Gemüsebrühe", 400.0, "ml", Aisle.PANTRY),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Zwiebel, Möhre und Kartoffeln würfeln.",
                "Zwiebeln in etwas Öl andünsten, dann Möhren und Kartoffeln dazugeben.",
                "Die roten Linsen kalt abspülen und mit in den Topf geben.",
                "Mit Gemüsebrühe aufgießen und ca. 20-25 Minuten köcheln lassen, bis die Linsen weich sind.",
                "Mit Salz, Pfeffer und einem Schuss Essig abschmecken."
            )
        ),
        Recipe(
            id = "l2",
            name = "Spaghetti Pomodoro",
            emoji = "🍝",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.LUNCH,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 20,
            skillLevel = 1,
            costPerServingEUR = 1.20,
            proteinGrams = 12,
            kcal = 450,
            ingredients = listOf(
                Ingredient("Spaghetti", 100.0, "g", Aisle.PANTRY),
                Ingredient("Tomaten in Dosen", 200.0, "g", Aisle.PANTRY),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Knoblauchzehe", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Parmesan", 15.0, "g", Aisle.DAIRY),
                Ingredient("Olivenöl", 1.0, "EL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Die Spaghetti in reichlich Salzwasser bissfest kochen.",
                "Währenddessen Zwiebel und Knoblauch fein hacken und in Olivenöl anschwitzen.",
                "Dosendomaten hinzugeben, leicht salzen, pfeffern und ca. 10 Minuten köcheln lassen.",
                "Die Spaghetti abgießen und direkt mit der Tomatensoße vermengen.",
                "Mit geriebenem Parmesan servieren."
            )
        ),
        Recipe(
            id = "l3",
            name = "Gefüllte Ofenkartoffel (Kumpir)",
            emoji = "🥔",
            cuisine = Cuisine.TURKISH,
            mealType = MealType.LUNCH,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 40,
            skillLevel = 1,
            costPerServingEUR = 1.40,
            proteinGrams = 10,
            kcal = 360,
            ingredients = listOf(
                Ingredient("Kartoffeln", 1.0, "groß", Aisle.PRODUCE),
                Ingredient("Mais aus der Dose", 30.0, "g", Aisle.PANTRY),
                Ingredient("Erbsen", 30.0, "g", Aisle.PRODUCE),
                Ingredient("Butter", 15.0, "g", Aisle.DAIRY),
                Ingredient("Gouda", 40.0, "g", Aisle.DAIRY),
                Ingredient("Quark", 50.0, "g", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Die Kartoffel gründlich waschen, mit einer Gabel mehrfach einstechen und bei 200°C im Ofen ca. 45-50 Minuten backen (oder ca. 8-10 Min. in der Mikrowelle).",
                "Die heiße Kartoffel der Länge nach aufschneiden, das Innere vorsichtig mit einer Gabel auflockern.",
                "Butter und geriebenen Gouda im Kartoffelbett verrühren, bis es schmilzt und cremig wird.",
                "Mit Mais, Erbsen und einem Klecks Quark füllen und warm genießen."
            )
        ),
        Recipe(
            id = "l4",
            name = "Italienischer Nudelsalat",
            emoji = "🥗",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.LUNCH,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 20,
            skillLevel = 1,
            costPerServingEUR = 1.60,
            proteinGrams = 11,
            kcal = 390,
            ingredients = listOf(
                Ingredient("Nudeln (Penne)", 80.0, "g", Aisle.PANTRY),
                Ingredient("Cherrytomaten", 50.0, "g", Aisle.PRODUCE),
                Ingredient("Mozzarella", 50.0, "g", Aisle.DAIRY),
                Ingredient("Basilikum", 0.25, "Bund", Aisle.PRODUCE),
                Ingredient("Olivenöl", 1.0, "EL", Aisle.PANTRY),
                Ingredient("Balsamico", 1.0, "EL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Penne-Nudeln in Salzwasser bissfest kochen, abgießen und mit kaltem Wasser abschrecken.",
                "Cherrytomaten halbieren, Mozzarella in kleine Würfel schneiden.",
                "Olivenöl, Balsamico-Essig, Salz und Pfeffer zu einem Dressing verrühren.",
                "Nudeln, Tomaten und Mozzarella in einer Schüssel mit dem Dressing vermengen.",
                "Mit frischen Basilikumblättern bestreut servieren (schmeckt kalt fantastisch)."
            )
        ),
        Recipe(
            id = "l5",
            name = "Gebratener Eierreis",
            emoji = "🍚",
            cuisine = Cuisine.CHINESE,
            mealType = MealType.LUNCH,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 20,
            skillLevel = 1,
            costPerServingEUR = 1.10,
            proteinGrams = 14,
            kcal = 420,
            ingredients = listOf(
                Ingredient("Reis (gekocht)", 150.0, "g", Aisle.PANTRY),
                Ingredient("Ei", 2.0, "Stück", Aisle.DAIRY),
                Ingredient("Erbsen", 50.0, "g", Aisle.PRODUCE),
                Ingredient("Möhre", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Sojasauce", 1.5, "EL", Aisle.PANTRY),
                Ingredient("Lauchzwiebel", 1.0, "Stück", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Möhre fein würfeln, Lauchzwiebel in Ringe schneiden.",
                "Möhrenwürfel und Erbsen in einer Pfanne mit etwas Öl ca. 5 Minuten braten.",
                "Gekochten Reis dazugeben und bei hoher Hitze unter Rühren 5 Minuten braten.",
                "Den Reis in der Pfanne beiseiteschieben, die Eier in die freie Fläche schlagen und verrühren, bis sie stocken.",
                "Das Ei unter den Reis mischen, mit Sojasauce verfeinern und die Lauchzwiebeln kurz unterheben."
            )
        ),

        // === SNACK ===
        Recipe(
            id = "s1",
            name = "Brezel mit Butter",
            emoji = "🥨",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.SNACK,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 5,
            skillLevel = 1,
            costPerServingEUR = 0.90,
            proteinGrams = 6,
            kcal = 260,
            ingredients = listOf(
                Ingredient("Brezel", 1.0, "Stück", Aisle.PANTRY),
                Ingredient("Butter", 15.0, "g", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Die Brezel horizontal aufschneiden.",
                "Beide Hälften mit weicher Butter bestreichen und wieder zusammensetzen."
            )
        ),
        Recipe(
            id = "s2",
            name = "Kräuterquark mit Karotten",
            emoji = "🥕",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.SNACK,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 10,
            skillLevel = 1,
            costPerServingEUR = 0.80,
            proteinGrams = 12,
            kcal = 150,
            ingredients = listOf(
                Ingredient("Magerquark", 150.0, "g", Aisle.DAIRY),
                Ingredient("Karotten", 2.0, "Stück", Aisle.PRODUCE),
                Ingredient("Kräuter (Schnittlauch, Petersilie)", 0.25, "Bund", Aisle.PRODUCE),
                Ingredient("Mineralwasser", 10.0, "ml", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Den Quark mit etwas spritzigem Mineralwasser cremig rühren.",
                "Die Kräuter fein hacken und unter den Quark heben. Mit Salz und Pfeffer abschmecken.",
                "Die Karotten schälen und in handliche Sticks schneiden.",
                "Die Karottensticks in den Quark dippen."
            )
        ),
        Recipe(
            id = "s3",
            name = "Tomate-Mozzarella Spieße",
            emoji = "🍢",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.SNACK,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 5,
            skillLevel = 1,
            costPerServingEUR = 1.30,
            proteinGrams = 8,
            kcal = 180,
            ingredients = listOf(
                Ingredient("Cherrytomaten", 6.0, "Stück", Aisle.PRODUCE),
                Ingredient("Mini-Mozzarella", 6.0, "Stück", Aisle.DAIRY),
                Ingredient("Olivenöl", 1.0, "TL", Aisle.PANTRY),
                Ingredient("Basilikum", 6.0, "Blätter", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Je eine Cherrytomate, ein Basilikumblatt und eine Mozzarellakugel auf einen kleinen Holzspieß stecken.",
                "Auf einem Teller anrichten, mit Salz und Pfeffer würzen und mit etwas Olivenöl beträufeln."
            )
        ),
        Recipe(
            id = "s4",
            name = "Hummus mit Gurkensticks",
            emoji = "🥒",
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.SNACK,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 5,
            skillLevel = 1,
            costPerServingEUR = 1.10,
            proteinGrams = 6,
            kcal = 190,
            ingredients = listOf(
                Ingredient("Kichererbsen aus der Dose", 100.0, "g", Aisle.PANTRY),
                Ingredient("Tahini", 1.0, "EL", Aisle.PANTRY),
                Ingredient("Zitrone", 0.25, "Stück", Aisle.PRODUCE),
                Ingredient("Gurke", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Olivenöl", 1.0, "TL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Kichererbsen abschütten, abspülen und mit Tahini, Zitronensaft, etwas Salz und Olivenöl cremig pürieren.",
                "Die Gurke in feine Sticks schneiden.",
                "Den Hummus in ein Schälchen füllen und mit Gurkensticks zum Dippen anrichten."
            )
        ),

        // === DINNER (Abendessen) ===
        Recipe(
            id = "d1",
            name = "Allgäuer Kässpätzle",
            emoji = "🧀",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 30,
            skillLevel = 2,
            costPerServingEUR = 1.80,
            proteinGrams = 22,
            kcal = 580,
            ingredients = listOf(
                Ingredient("Spätzle (trocken)", 100.0, "g", Aisle.PANTRY),
                Ingredient("Bergkäse", 80.0, "g", Aisle.DAIRY),
                Ingredient("Zwiebel", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Butter", 20.0, "g", Aisle.DAIRY),
                Ingredient("Milch", 50.0, "ml", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Spätzle in reichlich Salzwasser kochen, bis sie an die Oberfläche steigen.",
                "Die Zwiebel in Ringe schneiden und in einer Pfanne mit reichlich Butter goldbraun und leicht knusprig braten.",
                "Den Bergkäse fein reiben.",
                "Abwechselnd heiße Spätzle und geriebenen Käse in eine warme Form schichten, etwas Milch angießen, damit es saftig bleibt.",
                "Die geschmolzenen Kässpätzle mit den Röstzwiebeln bedecken und heiß servieren."
            )
        ),
        Recipe(
            id = "d2",
            name = "Kartoffelpuffer mit Apfelmus",
            emoji = "🥔",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 25,
            skillLevel = 2,
            costPerServingEUR = 1.00,
            proteinGrams = 6,
            kcal = 380,
            ingredients = listOf(
                Ingredient("Kartoffeln", 300.0, "g", Aisle.PRODUCE),
                Ingredient("Ei", 1.0, "Stück", Aisle.DAIRY),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Apfelmus", 100.0, "g", Aisle.PANTRY),
                Ingredient("Pflanzenöl", 2.0, "EL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Die Kartoffeln schälen und auf einer feinen Reibe reiben. Die Flüssigkeit über einem Sieb leicht ausdrücken.",
                "Die halbe Zwiebel ebenfalls reiben und mit Ei, Salz und Pfeffer unter die Kartoffeln mischen.",
                "Reichlich Öl in einer Pfanne erhitzen.",
                "Esslöffelweise Teig hineingeben, flachdrücken und von beiden Seiten knusprig braun braten.",
                "Auf Küchenpapier abtropfen lassen und mit Apfelmus servieren."
            )
        ),
        Recipe(
            id = "d3",
            name = "Vegetarisches Gemüse-Curry",
            emoji = "🍛",
            cuisine = Cuisine.INDIAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 30,
            skillLevel = 2,
            costPerServingEUR = 1.90,
            proteinGrams = 12,
            kcal = 410,
            ingredients = listOf(
                Ingredient("Reis (Basmati)", 80.0, "g", Aisle.PANTRY),
                Ingredient("Kokosmilch", 150.0, "ml", Aisle.PANTRY),
                Ingredient("Möhre", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Brokkoli", 100.0, "g", Aisle.PRODUCE),
                Ingredient("Currypulver", 1.0, "EL", Aisle.PANTRY),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Basmatireis nach Packungsanleitung gar kochen.",
                "Brokkoli in Röschen teilen, Möhre und Zwiebel klein schneiden.",
                "Zwiebeln in einer Pfanne anschwitzen, Gemüse hinzugeben und ca. 5 Minuten dünsten.",
                "Mit Currypulver bestäuben, kurz anrösten und dann mit Kokosmilch aufgießen.",
                "Ca. 10-12 Minuten köcheln lassen, bis das Gemüse bissfest gegart ist. Mit Salz abschmecken und mit Reis servieren."
            )
        ),
        Recipe(
            id = "d4",
            name = "Schupfnudeln mit Sauerkraut",
            emoji = "🍳",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.DINNER,
            vegetarian = false,
            pork = true,
            cookTimeMinutes = 20,
            skillLevel = 1,
            costPerServingEUR = 1.60,
            proteinGrams = 14,
            kcal = 430,
            ingredients = listOf(
                Ingredient("Schupfnudeln", 150.0, "g", Aisle.PANTRY),
                Ingredient("Sauerkraut", 150.0, "g", Aisle.PANTRY),
                Ingredient("Speckgewürfelt", 30.0, "g", Aisle.MEAT),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Butter", 10.0, "g", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Zwiebel fein würfeln.",
                "Zwiebeln und Speckwürfel in einer Pfanne anschwitzen.",
                "Das Sauerkraut hinzugeben und ca. 10 Minuten miterhitzen. Mit etwas Salz, Pfeffer und einer Prise Zucker abschmecken.",
                "In einer separaten Pfanne Schupfnudeln in Butter goldbraun anbraten.",
                "Die Schupfnudeln unter das Sauerkraut heben und zusammen servieren."
            )
        ),
        Recipe(
            id = "d5",
            name = "Linsensuppe (Mercimek Çorbası)",
            emoji = "🥣",
            cuisine = Cuisine.TURKISH,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 25,
            skillLevel = 1,
            costPerServingEUR = 1.10,
            proteinGrams = 16,
            kcal = 330,
            ingredients = listOf(
                Ingredient("Rote Linsen", 100.0, "g", Aisle.PANTRY),
                Ingredient("Zwiebel", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Karotte", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Tomatenmark", 1.0, "EL", Aisle.PANTRY),
                Ingredient("Gemüsebrühe", 500.0, "ml", Aisle.PANTRY),
                Ingredient("Zitrone", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Minze (getrocknet)", 0.5, "TL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Zwiebel und Karotte fein schneiden und in einem Topf mit etwas Öl andünsten.",
                "Tomatenmark dazugeben und kurz mit anrösten.",
                "Linsen gründlich waschen, in den Topf geben und mit heißer Gemüsebrühe ablöschen.",
                "Ca. 15-20 Minuten köcheln lassen, bis die Linsen zerfallen.",
                "Die Suppe fein pürieren, mit Salz, Pfeffer und getrockneter Minze abschmecken. Mit Zitronenspalten zum Auspressen servieren."
            )
        ),
        Recipe(
            id = "d6",
            name = "Bohnen-Tacos",
            emoji = "🌮",
            cuisine = Cuisine.MEXICAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 20,
            skillLevel = 1,
            costPerServingEUR = 1.80,
            proteinGrams = 15,
            kcal = 440,
            ingredients = listOf(
                Ingredient("Taco-Shells", 3.0, "Stück", Aisle.PANTRY),
                Ingredient("Kidneybohnen aus der Dose", 100.0, "g", Aisle.PANTRY),
                Ingredient("Eisbergsalat", 30.0, "g", Aisle.PRODUCE),
                Ingredient("Gouda", 30.0, "g", Aisle.DAIRY),
                Ingredient("Salsa-Sauce", 50.0, "ml", Aisle.PANTRY),
                Ingredient("Mais aus der Dose", 30.0, "g", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Die Kidneybohnen und den Mais abgießen, in einem kleinen Topf leicht erwärmen und nach Wunsch grob zerdrücken.",
                "Eisbergsalat in feine Streifen schneiden.",
                "Die Taco-Shells nach Packungsanleitung im Ofen kurz erwärmen.",
                "Die Tacos mit warmen Bohnen, Salatstreifen, Mais und Salsa füllen.",
                "Mit geriebenem Gouda-Käse bestreuen und direkt genießen."
            )
        ),
        Recipe(
            id = "d7",
            name = "Pizza Margherita",
            emoji = "🍕",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 30,
            skillLevel = 2,
            costPerServingEUR = 1.50,
            proteinGrams = 18,
            kcal = 520,
            ingredients = listOf(
                Ingredient("Pizzateig (fertig)", 0.25, "Rolle", Aisle.PANTRY),
                Ingredient("Tomatenpassata", 100.0, "ml", Aisle.PANTRY),
                Ingredient("Mozzarella", 100.0, "g", Aisle.DAIRY),
                Ingredient("Oregano", 1.0, "Prise", Aisle.PANTRY),
                Ingredient("Olivenöl", 1.0, "TL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Den Ofen auf 220°C Ober-/Unterhitze vorheizen.",
                "Den Pizzateig auf einem Backblech entrollen.",
                "Tomatenpassata gleichmäßig auf dem Teig verteilen, mit Salz und Oregano bestreuen.",
                "Mozzarella in feine Scheiben zupfen und darauf verteilen.",
                "Mit etwas Olivenöl beträufeln und ca. 12-15 Minuten backen, bis der Käse geschmolzen und der Rand knusprig braun ist."
            )
        ),
        Recipe(
            id = "d8",
            name = "Chow Mein Gemüsepfanne",
            emoji = "🍜",
            cuisine = Cuisine.CHINESE,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 25,
            skillLevel = 2,
            costPerServingEUR = 1.70,
            proteinGrams = 11,
            kcal = 410,
            ingredients = listOf(
                Ingredient("Mie-Nudeln", 80.0, "g", Aisle.PANTRY),
                Ingredient("Sojasprossen", 50.0, "g", Aisle.PRODUCE),
                Ingredient("Möhre", 1.0, "Stück", Aisle.PRODUCE),
                Ingredient("Paprika", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Sojasauce", 2.0, "EL", Aisle.PANTRY),
                Ingredient("Ingwer", 1.0, "Knopf", Aisle.PRODUCE)
            ),
            instructions = listOf(
                "Die Mie-Nudeln mit kochendem Wasser übergießen und ca. 5 Minuten ziehen lassen, dann abgießen.",
                "Möhre und Paprika in feine Streifen schneiden. Ingwer fein reiben.",
                "Möhren, Paprika und Ingwer in einer Pfanne oder einem Wok unter Rühren ca. 5 Minuten braten.",
                "Sojasprossen und Nudeln hinzufügen und weitere 2-3 Minuten braten.",
                "Mit reichlich Sojasauce ablöschen, vermengen und heiß servieren."
            )
        ),
        Recipe(
            id = "d9",
            name = "Mittelmeer-Gemüsepfanne",
            emoji = "🍆",
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.DINNER,
            vegetarian = true,
            pork = false,
            cookTimeMinutes = 25,
            skillLevel = 1,
            costPerServingEUR = 1.40,
            proteinGrams = 8,
            kcal = 280,
            ingredients = listOf(
                Ingredient("Zucchini", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Auberginen", 0.5, "Stück", Aisle.PRODUCE),
                Ingredient("Tomate", 2.0, "Stück", Aisle.PRODUCE),
                Ingredient("Feta", 50.0, "g", Aisle.DAIRY),
                Ingredient("Olivenöl", 1.5, "EL", Aisle.PANTRY),
                Ingredient("Kräuter der Provence", 1.0, "TL", Aisle.PANTRY)
            ),
            instructions = listOf(
                "Zucchini, Aubergine und Tomaten in gleichmäßige Würfel schneiden.",
                "Das Olivenöl in einer Pfanne erhitzen. Erst die Auberginenwürfel anbraten, da sie am längsten brauchen (ca. 5 Min.).",
                "Zucchini und Tomaten hinzugeben und weitere 7 Minuten dünsten.",
                "Mit Salz, Pfeffer und Kräutern der Provence kräftig abschmecken.",
                "Die Gemüsepfanne auf einen Teller geben und den Feta darüber zerbröseln."
            )
        ),
        Recipe(
            id = "d10",
            name = "Schweinefilet mit Kräuterkruste",
            emoji = "🥩",
            cuisine = Cuisine.GERMAN,
            mealType = MealType.DINNER,
            vegetarian = false,
            pork = true,
            cookTimeMinutes = 45,
            skillLevel = 3,
            costPerServingEUR = 3.50,
            proteinGrams = 28,
            kcal = 490,
            ingredients = listOf(
                Ingredient("Schweinefilet", 150.0, "g", Aisle.MEAT),
                Ingredient("Kartoffeln", 200.0, "g", Aisle.PRODUCE),
                Ingredient("Senf (mittelscharf)", 1.0, "EL", Aisle.PANTRY),
                Ingredient("Semmelbrösel", 20.0, "g", Aisle.PANTRY),
                Ingredient("Kräutermischung", 2.0, "EL", Aisle.PRODUCE),
                Ingredient("Butter", 15.0, "g", Aisle.DAIRY)
            ),
            instructions = listOf(
                "Den Backofen auf 180°C vorheizen.",
                "Die Kartoffeln schälen und in Salzwasser kochen.",
                "Das Schweinefilet salzen, pfeffern und in einer Pfanne von allen Seiten scharf anbraten.",
                "Aus weicher Butter, Semmelbröseln, Kräutern und Senf eine Paste rühren.",
                "Die Paste auf dem Filet verteilen und im Ofen ca. 15-20 Minuten garen, bis eine schöne Kräuterkruste entsteht. Mit Salzkartoffeln servieren."
            )
        )
    )
}
