(ns cljs-snake.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as rdom]))


(def snake1 {:trails (list [10 10] [9 10] [8 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [50 50]})

(comment
  (def snake1 {:trails (list [10 10] [9 10] [8 10])
               :dead false
               :dir [1, 0] ;; direction
               :food [50 50]}))

;; -------------------------
;; state
;(defstruct Point :x :y)

(def settings {:tilesNr 50
               :tileSize 12
               :hiddenLayers 3
               :hiddenNodes 2})


(def app-state (r/atom { :manualMode true
                         :highScore 0
                         :mutationRate 0.05
                         :fps 100
                         :population []}))  ;; elenco di snakes


(defn chessboard-tiles-nr "nr celle scacchiera" [settings]
  (list
    (int (/ (:chessboardWidth settings) (:squareSize settings)))
    (int (/ (:chessboardHeight settings) (:squareSize settings)))))


(defn get-random-tile [settings]
  (let [tiles (chessboard-tiles-nr settings)]
    (list (rand-int (first tiles )) (rand-int (second tiles)))))


(defn create-snake [settings]
  snake1)

(defn snake-move [snake settings]
  (let [trails (:trails snake) dir (:dir snake)]
    (assoc snake :trails
      ;; appendiamo nuova testa
      (conj trails (vec (map + (first trails) dir))))))


(defn create-population [n]
 {
   :snakes (map #(create-snake settings) (range n))
   :bestSnake 0
   :gen 0
   :bestSnakeScore 0
   :sameBest 0})



(defn draw-canvas-contents [ canvas]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.beginPath ctx)
    (.moveTo ctx 0 0)
    (.lineTo ctx w h)
    (.moveTo ctx w 0)
    (.lineTo ctx 0 h)
    (.stroke ctx)))



(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])



;; -------------------------
;; Views
;;
(defn canvas-component [settings a b c]
 (let [state (r/atom {})] ;; you can include state
   (r/create-class  {
                     :component-did-mount (fn [this] (draw-canvas-contents
                       ;; per ora non so fare altro ... ci sarà un altro modo senza usare getElementById
                                                       (rdom/dom-node  (. js/document (getElementById "canvas")))))
                     :display-name "canvas-component"
                      ;; note the keyword for this method
                     :reagent-render  (fn [a b c]
                                          [:div {:style {:margin-left "260px"}}
                                            [:canvas {:id "canvas"
                                                      :style {:width (str (*  (:tilesNr settings) (:tileSize settings)) "px")
                                                              :height (str (*  (:tilesNr settings) (:tileSize settings)) "px")}}]])})))




(defn home-page []
 [:div
    [:h2 "Snake in clojurescript"]
    [:div
       [:div {:style {:width "250px"
                      :float "left"}}
        [:input {:type "checkbox"}]
        [:br]
        [:input {:type "button" :value "start"
                 :on-click (fn[x] (js/console.log "start clicked ..."))}]]
       [canvas-component settings "a" "b" "c"]]
    [simple-component]])


;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
