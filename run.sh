function cycle {
    number=12

    if [ ! $# -eq 1 ]; then
        if [[ -n ${2//[0-9]/} ]]; then
            echo "Invalid int value for number of nodes"
        else
            number=$2
        fi
    fi

    cd graphs
    ./gengraph cycle ${number} -width 1 -format dot -permute | dot -Kneato > tmp.dot
    cd ..
}

if [ ! $# -eq 0 ]; then
    if [ "$1" = "cycle" ]; then
        cycle "$@"
        gradle build
        gradle run
    fi
else
    echo "Invalid type of graph."
    echo "Usage: ./run.sh <type> [args...]"
fi