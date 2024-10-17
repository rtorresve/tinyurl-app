#!/bin/bash
host_url="http://infotor.online/tiny/create-url"
json_files=(data/post_data*.json)

random_json_file=${json_files[RANDOM % ${#json_files[@]}]}

ab -n 30000 -c 7 -p "$random_json_file" -T application/json "$host_url"