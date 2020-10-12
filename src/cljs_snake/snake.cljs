(ns cljs-snake.snake
    (:require
      [reagent.core :as r]
      [reagent.dom :as rdom]))

(def snake2 {:trails (list [50 10] [49 10] [48 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [50 50]})


(defn head [snake]
   "head of the snake"
   (first (:trails snake)))
