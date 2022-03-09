#!/bin/bash

config=./server/sample-configs/ds-sample-config01.xml
algorithm=LRR

while getopts c:a: flag
do
    case "${flag}" in
        c) config=${OPTARG};;
        a) algorithm=${OPTARG};;
    esac
done

osascript <<EOD
tell application "iTerm"
    tell current window
        -- create tab to run api
        create tab with default profile

        tell current session
            split vertically with default profile
        end tell

        tell first session of current tab
            write text "./server/ds-server -c $config -n -v brief"
        end tell

        tell second session of current tab
           write text "sleep 2"
           write text "gradle run --args='$algorithm'"
        end tell

    end tell
end tell

EOD
