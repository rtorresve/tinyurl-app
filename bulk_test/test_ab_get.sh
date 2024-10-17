#!/bin/sh
host_url="http://infotor.online/tiny"

mapfile -t urls < ./data//short_urls.txt
random_short=${urls[$RANDOM % ${#urls[@]}]}

ab -n 30000 -c 7 "$host_url"/"$random_short"