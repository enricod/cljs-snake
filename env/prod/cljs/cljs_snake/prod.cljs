(ns cljs-snake.prod
  (:require
    [cljs-snake.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
