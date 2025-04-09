export default function Meal() {

    const dish = {
        name: "Prato Carne",
        description: "Carne com arroz e batata frita",
        price: 2.8,
        type: "Carne",
    }

    return (
        <div className="overflow-x-auto">
        <table className="table table-zebra w-full border-collapse">
            <tbody>
            <tr>
                <td className="font-medium border p-3 bg-base-200 w-1/4">Dia</td>
                <td className="border p-3 bg-base-100">Semana de 07/04 a 10/04/2025</td>
            </tr>

            <tr>
                <td className="font-medium border p-3 bg-base-200">Ementa</td>
                <td className="border p-3 bg-base-100">
                    <span>{dish.name} - {dish.description} | {dish.price}$</span>
                </td>
            </tr>
            </tbody>
        </table>
        </div>
  )
}