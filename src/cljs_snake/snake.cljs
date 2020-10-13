(ns cljs-snake.snake
    (:require
      [reagent.core :as r]
      [reagent.dom :as rdom]))



(def snake1 {:trails (list [10 10] [9 10] [8 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [21 10]})


(def snake2 {:trails (list [50 10] [49 10] [48 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [50 50]})

(defn shead [snake]
   "head of the snake"
   (first (:trails snake)))


(defn drop-tail [snake]
  "rimuove ultimo pezzo coda"
  (let [trails (:trails snake)]
   (assoc snake :trails
     (drop-last  trails))))



(defn get-random-tile [snake settings]
  "FIXME da verificare, perchè non deve tornare alcuno 0"
  (let [n (:tilesNr settings)]
    [(rand-int n) (rand-int n)]))


(defn new-food [snake settings]
  "riposiziona il cibo FIXME controllare che posizione sia valida"
  (assoc snake :food (get-random-tile snake settings)))


(defn has-eaten? [snake]
  "torna true se ha mangiato il frutto"
  (= (shead snake) (:food snake)))


(defn dead? [snake settings]
  "FIXME controllare che non vada contro se stesso"
  (let [h1 (shead snake)
        h1x (first h1)
        h1y (second h1)
        tilesNr (:tilesNr settings)]
   (or    (= h1x (inc tilesNr))
          (= h1x 0)
          (= h1y 0)
          (= h1y (inc tilesNr)))))


(defn alive? [snake settings] (not (dead? snake settings)))
