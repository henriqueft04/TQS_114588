export default function RestaurantCard() {
    return (
        <div className="card w-96 bg-base-100 card-md shadow-sm">
            <div className="card-body">
                <h2 className="card-title">Restaurant Name</h2>
                <p>Restaurant Description</p>
                <div className="justify-end card-actions">
                <button className="btn btn-primary">Navigate there</button>
                </div>
            </div>
        </div>
    )
}