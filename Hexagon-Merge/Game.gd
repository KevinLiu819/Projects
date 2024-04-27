extends Node2D

enum { FOLLOW_TILE_A, FOLLOW_TILE_B, FOLLOW_NONE }
const START_POS = Vector2i(1700, 1000)
const EMPTY_CELL = Vector2i(6, 0)

var tile_scene : PackedScene = preload("res://Tile.tscn")
var tile

var rng = RandomNumberGenerator.new()

var score : int = 0

# Called when the node enters the scene tree for the first time.
func _ready():
	create_tile()

func create_tile():
	tile = tile_scene.instantiate()
	tile.tile_a = rng.randi_range(1, 6)
	tile.tile_b = rng.randi_range(1, 5)
	if tile.tile_b >= tile.tile_a:
		tile.tile_b += 1
	add_child(tile)

func delete_tile():
	tile.queue_free()

func upgrade_hex(hex):
	var next_hex = $Board.get_cell_atlas_coords(0, hex) + Vector2i(1, 0)
	if next_hex == EMPTY_CELL:
		erase_hexes({ hex: true })
		return false
	else:
		$Board.set_cell(0, hex, 0, next_hex)
		$Board.set_cell(1, hex, 1, next_hex)
		return true

func erase_hexes(hexes):
	for hex in hexes.keys():
		$Board.set_cell(0, hex, 0, EMPTY_CELL)
		$Board.erase_cell(1, hex)

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	pass

"""
Handle moves
"""

func check_valid_drop(tile) -> bool:
	var mouse_pos = get_local_mouse_position()
	var mouse_hex = $Board.local_to_map(mouse_pos)
	var hex_a
	var hex_b
	print(mouse_hex)
	if tile.follow_mouse == FOLLOW_TILE_A:
		hex_a = mouse_hex
		hex_b = mouse_hex + Vector2i(1, 0)
	elif tile.follow_mouse == FOLLOW_TILE_B:
		hex_a = mouse_hex - Vector2i(1, 0)
		hex_b = mouse_hex
	else:
		return false
	if valid_move(hex_a, hex_b):
		var tile_a = tile.tile_a - 1
		var tile_b = tile.tile_b - 1
		$Board.set_cell(0, hex_a, 0, Vector2i(tile_a, 0))
		$Board.set_cell(1, hex_a, 1, Vector2i(tile_a, 0))
		$Board.set_cell(0, hex_b, 0, Vector2i(tile_b, 0))
		$Board.set_cell(1, hex_b, 1, Vector2i(tile_b, 0))
		delete_tile()
		check_merge(hex_a)
		check_merge(hex_b)
		check_merge(hex_a)
		create_tile()
		return true
	else:
		tile.position = START_POS
		return false

func valid_move(hex_a, hex_b) -> bool:
	var board_hexes = $Board.get_used_cells(0)
	if not board_hexes.has(hex_a) or not board_hexes.has(hex_b):
		return false
	return $Board.get_cell_atlas_coords(0, hex_a) == EMPTY_CELL \
			and $Board.get_cell_atlas_coords(0, hex_b) == EMPTY_CELL

"""
Handle merges
"""

func check_merge(hex):
	var val = $Board.get_cell_atlas_coords(1, hex).x
	var visited = Dictionary()
	floodfill(hex, val, visited)
	visited.erase(hex)
	if not visited.is_empty():
		score += (val + 1) * (visited.size() + 1)
		erase_hexes(visited)
		if upgrade_hex(hex):
			check_merge(hex)

func floodfill(hex, val, visited):
	visited[hex] = true
	for neighbor_hex in $Board.get_surrounding_cells(hex):
		if $Board.get_used_cells(1).has(neighbor_hex) \
				and $Board.get_cell_atlas_coords(1, neighbor_hex).x == val \
				and not visited.has(neighbor_hex):
			floodfill(neighbor_hex, val, visited)

