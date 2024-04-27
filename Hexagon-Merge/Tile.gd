extends Node2D

enum { FOLLOW_TILE_A, FOLLOW_TILE_B, FOLLOW_NONE }
const START_POS = Vector2i(1700, 1000)

var tile_a : int
var tile_b : int
var follow_mouse = FOLLOW_NONE
var area_a_hover : bool = false
var area_b_hover : bool = false

signal release_tile

# Called when the node enters the scene tree for the first time.
func _ready():
	var x_pos_a = (tile_a - 1) * 200
	$HexA.region_rect = Rect2i(x_pos_a, 0, 200, 200)
	$HexA/NumA.region_rect = Rect2i(x_pos_a, 0, 200, 200)
	var x_pos_b = (tile_b - 1) * 200
	$HexB.region_rect = Rect2i(x_pos_b, 0, 200, 200)
	$HexB/NumB.region_rect = Rect2i(x_pos_b, 0, 200, 200)
	position = START_POS


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	if follow_mouse == FOLLOW_TILE_A:
		position = get_global_mouse_position()
	elif follow_mouse == FOLLOW_TILE_B:
		position = get_global_mouse_position() - Vector2(165, 0)

"""
Mouse events
"""

func _input(event):
	var mouse_pos = get_global_mouse_position()
	if event.is_action_pressed("click"):
		handle_mouse_pressed()
	elif event.is_action_released("click"):
		handle_mouse_released()

func handle_mouse_pressed():
	if area_a_hover:
		follow_mouse = FOLLOW_TILE_A
	elif area_b_hover:
		follow_mouse = FOLLOW_TILE_B

func handle_mouse_released():
	print(get_parent().check_valid_drop(self))
	follow_mouse = FOLLOW_NONE

"""
Mouse hover signals
"""

func _on_area_a_mouse_entered():
	area_a_hover = true

func _on_area_a_mouse_exited():
	area_a_hover = false

func _on_area_b_mouse_entered():
	area_b_hover = true

func _on_area_b_mouse_exited():
	area_b_hover = false
